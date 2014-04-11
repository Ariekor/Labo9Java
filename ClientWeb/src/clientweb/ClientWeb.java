// Client web
// Fait par : Simon Bouchard et Isabelle Angrignon
// Client qui transfert un fichier binaire sur le disque

package clientweb;

import java.net.*;
import java.io.*;
import java.util.concurrent.TimeoutException;

public class ClientWeb {
    
    int port = 80;
    final int NUMPORTMAX = 65535;
    final String PATH = "C:\\Client";
    BufferedReader reader;
    PrintWriter writer;
    Socket soc;
    final int DELAI = 500;
    
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
        EtablirConnexion();
        RecevoirTexteServeur();
        EnvoyerRequete();
          
    }
    
    void EtablirConnexion() throws Exception
    {
        soc = new Socket("localhost",port);
        try
        {
            reader = new BufferedReader(
                    new InputStreamReader( soc.getInputStream() ) );
            writer = new PrintWriter(
                    new OutputStreamWriter( soc.getOutputStream() ),true );
        }
        catch (Exception e){System.err.println(e.getMessage());}
    }
    
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
        catch ( SocketTimeoutException e) {} 
    }
    
    void EnvoyerRequete () throws Exception
    {
        String s = "";
        System.out.println();
        System.out.print("Fichier a récupérer : ");
        BufferedReader readConsole = new BufferedReader(
					new InputStreamReader(System.in));
        s = readConsole.readLine();
        writer.println("Get " + s);
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
