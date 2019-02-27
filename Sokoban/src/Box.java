public class Box {
    private double posX,posY;
    private boolean estPlace = false;

    public Box(double posX, double posY)
    {
        this.posX = posX;
        this.posY = posY;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }
    public void descendre(boolean canDoThis,double valeur)
    {
        if(canDoThis) {
            this.posY+=valeur;
        }
    }
    public void monter(boolean canDoThis, double valeur)
    {
        if(canDoThis) {
            this.posY -= valeur;
        }
    }
    public void gauche(boolean canDoThis,double valeur)
    {
        if(canDoThis) {
            this.posX-=valeur;
        }
    }
    public void droite(boolean canDoThis,double valeur)
    {
        if(canDoThis){
            this.posX+=valeur;
        }
    }

    public void setEstPlace(boolean estPlace)
    {
        this.estPlace = estPlace;
    }

    public boolean getEstPlace(){return this.estPlace;}
}
