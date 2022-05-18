/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package paintbasico2d;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author danielgs
 */
public class ColorCellRender implements ListCellRenderer<Color>{

    @Override
    public Component getListCellRendererComponent(JList<? extends Color> jlist, Color e, int i, boolean bln, boolean bln1) {
        ColorPanel panel = new ColorPanel(e);
        
        return panel;
        
    }
    
    
    
    
    
}
