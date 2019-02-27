public class Socle {
    private double posX,posY;
    private boolean estUtilise = false;

    public Socle(double posX, double posY)
    {
        this.posX = posX;
        this.posY = posY;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosX() {
        return posX;
    }

    public boolean getEstUtilise(){return this.estUtilise;}
    public void setEstUtilise(boolean estUtilise)
    {
        this.estUtilise = estUtilise;
    }

}


