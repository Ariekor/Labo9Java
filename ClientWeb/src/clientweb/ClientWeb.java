// Client web
// Fait par : Simon Bouchard et Isabelle Angrignon
// Client qui transfert un fichier binaire sur le disque

package clientweb;

import java.net.*;
import java.io.*;
import java.util.concurrent.TimeoutException;

public class ClientWeb {
    
    final int DELAI = 500;
    final int NUMPORTMAX = 65535;
    final String REUSSITE = "200";
    int port = 80;
    Socket soc;
    //flux textes
    BufferedReader reader;
    PrintWriter writer;
    //flux binaires
    BufferedInputStream in;
    BufferedOutputStream out;
        
    void SetPort(int p)
    {
        if (p < NUMPORTMAX && p > 0)
        {
            this.port = p;
        }
    }
    
    ClientWeb(String tab[])
    {
        try
        {
            int p = Integer.parseInt(tab[0]);
            SetPort(p);
        }
        catch (Exception e) { /*Fait rien, utilise le port par défaut*/ }
    }
    
    void Traitement() throws Exception
    {
        EtablirConnexion();                   // Le nom le dit...
        RecevoirTexteServeur();               // Affiche le contenu téléchargable et les sous-dossiers
        String fichier = EnvoyerRequete();    // On récupère aussi le nom du fichier qu'on télécharge     
        String code = RecevoirLigneTexteServeur(); // On récupère aussi le code d'état de la requête
        if  (code.equals(REUSSITE))               // On ne crée le fichier qu'en cas de réussite
        {
            TraiterRequete(fichier);  //on crée ou écrase loalement le fichier téléchargé
        }                 
    }
     
    void EtablirConnexion() throws Exception
    {
        soc = new Socket("localhost",port);
        try
        {
            // instancier les flux textes
            reader = new BufferedReader(
                    new InputStreamReader( soc.getInputStream() ) );
            writer = new PrintWriter(
                    new OutputStreamWriter( soc.getOutputStream() ),true );
        }
        catch (Exception e){System.err.println(e.getMessage());}
    }
    
    //Recoit une série de ligne tant que DELAI non atteint
    //On ne peut se fier à "ligne non vide" à cause du prompt => qui est "print()"
    void RecevoirTexteServeur() throws Exception
    {
        String s = " ";          
        try
        {
             soc.setSoTimeout(DELAI);  
             while (s != null)
             {
                 s = reader.readLine();
                 System.out.println(s);
             }
        }
        catch ( SocketTimeoutException e) {/*Permet de continuer malgré "=>" non reçu.*/} 
    }
    
    //On recoit une seule ligne qui contient le code de réussite ou d'échec de la requête envoyée
    String RecevoirLigneTexteServeur() throws Exception
    {
        String code = "ERR";
        try
        {      
            code = reader.readLine();
            System.out.println(code);            
        }
        catch ( SocketTimeoutException e) {} 
        return code.split(" ")[0]; // 
    }
    
    void TraiterRequete(String fichier)
    {
        int b = -1;
            boolean pasFini = true;
            try
            {
                // instancier les flux binaires
                in = new BufferedInputStream(soc.getInputStream());
                out = new BufferedOutputStream(
                        new FileOutputStream(fichier));
                //Transfert du contenu vers fichier
                while (pasFini)
                {
                    b = in.read();      //Lit un bit à la fois
                    if(b != -1)
                    {
                        out.write(b);   //Écrit un bit à la fois
                    }
                    else
                    {
                        pasFini = false;
                    }
                }
                in.close();
                out.close();
            }
            catch(IOException e) { e.printStackTrace(); }
    }
    //Lit la requête à la console et la transfert au serveur.
    String EnvoyerRequete () throws Exception
    {         
        System.out.println();       
        System.out.print("Fichier a récupérer : "); //remplace le prompt
        BufferedReader readConsole = new BufferedReader(
					new InputStreamReader(System.in));
        String s = readConsole.readLine();
        writer.println("Get " + s);
        return s;
    }
   
    public static void main(String[] args)
    {
        ClientWeb client = new ClientWeb(args);
        try
        {
            client.Traitement();
        }
        catch(Exception e )
        {
            System.out.println("J'ai pas trouver le serveur");
        } 
    }
}
