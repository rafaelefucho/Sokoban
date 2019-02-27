import javafx.scene.image.ImageView;

public class ImageTuile
{
    private char symbole;
    private String url;

    public ImageTuile(char symbole, String url)
    {
        this.symbole = symbole;
        this.url = url;
    }

    public char getSymbole(){return this.symbole;}
    public String getUrl(){return this.url;}
}
