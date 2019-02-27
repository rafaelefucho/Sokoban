

public class Personnage {
    private double posX,posY;
    char direction;

    public Personnage( double posX, double posY)
    {
        this.posX = posX;
        this.posY = posY;
        this.direction = 'd';
    }

    public Personnage() {

    }

    public Personnage(Personnage personnage) {
        this.posX = personnage.getPosX();
        this.posY = personnage.getPosY();
        this.direction = personnage.direction;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public char getDirection(){return this.direction;}

    public void descendre(boolean canDoThis,double valeur)
    {
        if(canDoThis)
            this.posY+=valeur;
            this.direction = 'd';

    }
    public void monter(boolean canDoThis,double valeur)
    {
        if(canDoThis)
            this.posY -= valeur;
            this.direction = 'u';

    }
    public void gauche(boolean canDoThis,double valeur)
    {
        if(canDoThis)
            this.posX-=valeur;
            this.direction = 'l';

    }
    public void droite(boolean canDoThis,double valeur)
    {
        if(canDoThis)
            this.posX+=valeur;
            this.direction = 'r';

    }
}
