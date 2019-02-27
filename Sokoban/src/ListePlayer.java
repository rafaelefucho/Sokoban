import java.io.*;
import java.util.ArrayList;
public class ListePlayer
{
    private ArrayList<Player> alPlayer;
    private Player playerActif;

    public ListePlayer()
    {
        alPlayer = new ArrayList<>();
        loadPlayer();
    }

    public boolean playerConnecting(String pseudo)
    {
        for(Player p : alPlayer)
        {
            if (p.pseudo.equals(pseudo)) {
                playerActif = p;
                return true;
            }
        }
        return false;
    }

    public boolean playerRegistering(String pseudo)
    {
        for(Player p : alPlayer)
        {
            if (p.pseudo.equals(pseudo)) {
                return false;
            }
        }
        this.alPlayer.add(new Player(pseudo,1));
        playerActif = this.alPlayer.get(this.alPlayer.size()-1);
        return true;
    }
    public void loadPlayer()
    {
        try
        {
//            FileReader fileReader = new FileReader("C:\\Users\\Maxime\\IdeaProjects\\Sokoban\\src\\Profile\\profile.data");
            FileReader fileReader = new FileReader("/home/administrateur/Téléchargements/Sokoban/Sokoban/src/Profile/profile.data");
            BufferedReader reader = new BufferedReader(fileReader);

            String ligne;
            while ((ligne = reader.readLine())!=null){
                alPlayer.add(new Player(ligne.split(":")[0],Integer.parseInt(ligne.split(":")[1])));
            }

        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void savePlayer()
    {
        try
        {
            FileWriter fileWriter = null;
            if (System.getProperty("os.name").toLowerCase().contains("win")){
                fileWriter = new FileWriter("C:\\Users\\Maxime\\IdeaProjects\\Sokoban\\src\\Profile\\profile.data");
            }
            else {

                String current = new java.io.File( "." ).getCanonicalPath();
                System.out.println("Current dir:"+current);

                fileWriter = new FileWriter(current+"/Sokoban/src/Profile/profile.data");
            }

            BufferedWriter writer = new BufferedWriter(fileWriter);

            for(Player p : alPlayer)
                writer.write(p.pseudo + ":" + p.level +"\n");

            writer.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public Player getPlayerActif() {
        return playerActif;
    }

    public ArrayList<Player> getAlPlayer() {
        return alPlayer;
    }

    public class Player
    {
        private String pseudo;
        private int level;

        private Player(String pseudo,int level)
        {
            this.pseudo = pseudo;
            this.level = level;
        }
        public int getLevel() {
            return level;
        }

        public String getPseudo() {
            return pseudo;
        }

        public void setLevel(int level) {
            if(level > this.level)
                this.level = level;
        }

        public void setPseudo(String pseudo) {
            this.pseudo = pseudo;
        }



    }
}

