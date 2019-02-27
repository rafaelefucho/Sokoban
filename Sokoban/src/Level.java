import java.io.*;

public class Level {
    private char[][] tabChar;
    boolean locked;
    public Level(int numLvl)
    {
        this.locked = true;
        lectureFicher(numLvl);
    }

    public char[][] lectureFicher(int numLvl)
    {

        BufferedReader reader = new BufferedReader(createFileReader(numLvl));
        this.tabChar = new char[compteLigne(new BufferedReader(createFileReader(numLvl)))][compteCol(new BufferedReader(createFileReader(numLvl)))];
        String ligne;

        try {
            for(int i = 0; i < tabChar.length; i++)
            {
                ligne = reader.readLine();
                for(int j = 0; j < tabChar[0].length; j++)
                {
                    tabChar[i][j] = ligne.charAt(j);
                }

            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }


        return tabChar;
    }
    private FileReader createFileReader(int numLvl)
    {
        try
        {
//            return new FileReader("C:\\Users\\eMaxim\\IdeaProjects\\Sokoban\\src\\lvl\\Level"+numLvl+".data");
//            File folder = new File("/home/administrateur/Téléchargements/Sokoban/Sokoban/src/lvl/Level");
//            File[] listOfFiles = folder.listFiles();
            return new FileReader("/home/administrateur/Téléchargements/Sokoban/Sokoban/src/lvl/Level"+numLvl+".data");
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    private int compteLigne(BufferedReader reader)
    {
        int cptLigne = 0;
        try {
            while (reader.readLine()!=null) {
                cptLigne++;
            }
        }catch (IOException e )
        {
            e.printStackTrace();
        }
        return cptLigne;
    }

    private int compteCol(BufferedReader reader)
    {
        String line;
        int maxChar = 0;
        try {
            while ((line = reader.readLine()) != null) {
                if(line.length() > maxChar)
                    maxChar = line.length();
            }
        }catch (IOException e )
        {
            e.printStackTrace();
        }
        return maxChar;
    }

    public String toString()
    {
        String sRet= "";
        for(int i = 0; i < tabChar.length; i++)
        {
            for(int j = 0; j < tabChar[0].length; j++)
            {
                sRet+=tabChar[i][j];
            }
            sRet+="\n";
        }
        return sRet;
    }

    public char[][] getTabChar(){return this.tabChar;}

    public boolean isLocked() {
        return locked;
    }
    public void unlockLevel()
    {
        this.locked = false;
    }
}
