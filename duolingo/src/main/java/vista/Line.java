package vista;

import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Line extends Path {
    
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    
    public Line() {
        this(0, 0, 0, 0);
    }
    
    public Line(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        
        // Configurar la línea
        setStroke(Color.valueOf("#4a69bd"));
        setStrokeWidth(2);
        
        // Crear el path
        MoveTo moveTo = new MoveTo(startX, startY);
        LineTo lineTo = new LineTo(endX, endY);
        getElements().addAll(moveTo, lineTo);
    }
    
    public void setStartX(double startX) {
        this.startX = startX;
        ((MoveTo) getElements().get(0)).setX(startX);
    }
    
    public void setStartY(double startY) {
        this.startY = startY;
        ((MoveTo) getElements().get(0)).setY(startY);
    }
    
    public void setEndX(double endX) {
        this.endX = endX;
        ((LineTo) getElements().get(1)).setX(endX);
    }
    
    public void setEndY(double endY) {
        this.endY = endY;
        ((LineTo) getElements().get(1)).setY(endY);
    }
}