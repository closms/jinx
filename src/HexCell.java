
import java.awt.*;
import java.awt.event.*;

/**
 * The HexCell class represents a single Hexagon on the Hex board.
 *
 * These are the points of on a Hexagon.
 *
 * <pre>
 *
 *    b     c
 *     *---*
 *    /     \
 * a *       * d
 *    \     /
 *     *---*
 *    f     e 
 *
 * </pre>
 *
 */

public class HexCell {

    int x, y;
    int centrex, centrey;
    int ax, ay;
    int bx, by;
    int cx, cy;
    int dx, dy;
    int ex, ey;
    int fx, fy;
    Color cDotColor = Color.BLACK;
    boolean bFill = false;
    boolean bShade = false;
    boolean bX = false;
    int dotx, doty, dotside;

    int xpts[];
    int ypts[];

    double abm, cdm, dem, fam;
    double abb, cdb, deb, fab;

    boolean bDrawAb = true;
    boolean bDrawBc = true;
    boolean bDrawCd = true;

    public boolean drawArrows = false;

    /**
     * Create a new HexCell, x and y is the position of the cell on the hex
     * board.
     *
     * setPoints must be called before the cell will be drawn, the
     * parameters are the six points of the hex cell.
     *
     * The HexCanvas computes these points and created all the necessary
     * hex cells.
     */


    HexCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPoints(
            int ax, int ay,
            int bx, int by,
            int cx, int cy,
            int dx, int dy,
            int ex, int ey,
            int fx, int fy )
    {

        this.ax = (int)ax; this.ay = (int)ay;
        this.bx = (int)bx; this.by = (int)by;
        this.cx = (int)cx; this.cy = (int)cy;
        this.dx = (int)dx; this.dy = (int)dy;
        this.ex = (int)ex; this.ey = (int)ey;
        this.fx = (int)fx; this.fy = (int)fy;

        xpts = new int[6];
        ypts = new int[6];
        xpts[0] = this.ax; ypts[0] = this.ay;
        xpts[1] = this.bx; ypts[1] = this.by;
        xpts[2] = this.cx; ypts[2] = this.cy;
        xpts[3] = this.dx; ypts[3] = this.dy;
        xpts[4] = this.ex; ypts[4] = this.ey;
        xpts[5] = this.fx; ypts[5] = this.fy;

        dotside = (int)(cx-bx);
        centrex = (int)bx+(dotside/2); centrey = (int)ay;
        dotx = (int)bx; doty = (int)ay - (dotside/2);

        abm = ((double)(by-ay))/((double)(bx-ax));
        abb = (double)ay-abm*(double)ax;
        cdm = ((double)(dy-cy))/((double)(dx-cx));
        cdb = (double)cy-cdm*(double)cx;
        dem = ((double)(ey-dy))/((double)(ex-dx));
        deb = (double)dy-dem*(double)dx;
        fam = ((double)(ay-fy))/((double)(ax-fx));
        fab = (double)fy-fam*(double)fx;
    }

    void paint(Graphics g) {
        /* draw shading */
        if( bShade ) {
            Color c = g.getColor();
            g.setColor( Color.LIGHT_GRAY );
            g.fillPolygon( xpts, ypts, 6 );
            g.setColor( c );
        }

        /* shade according to voltage */

        double bluevolt = Globals.gameBoard.voltage[x+1][y+1];
        double redvolt = bluevolt * -1;
        redvolt += 1;
        redvolt /= 2;  /* clamp voltage between 0.0 and 1.0 */
        bluevolt += 1;
        bluevolt /= 2;  /* clamp voltage between 0.0 and 1.0 */
        Color oldc = g.getColor();
        Color c = new Color( (float) redvolt,
                             (float) 0,
                             (float) bluevolt );
        g.setColor( c );
        g.fillPolygon( xpts, ypts, 6 );
        g.setColor( oldc );

        /* draw top three lines. */
        if( bDrawAb )
            g.drawLine( ax, ay, bx, by );
        if( bDrawBc )
            g.drawLine( bx, by, cx, cy );
        if( bDrawCd )
            g.drawLine( cx, cy, dx, dy );
        //		g.drawLine( dx, dy, ex, ey );
        //		g.drawLine( ex, ey, fx, fy );
        //		g.drawLine( fx, fy, ax, ay );
        if( bFill ) {
            Color oc = g.getColor();
            g.setColor(cDotColor);
            g.fillOval( dotx, doty, dotside, dotside );
            g.setColor(oc);
        }
        if (Globals.gameBoard.isOccupied(x,y)) {
            Color oc = g.getColor();
            g.setColor(Color.BLACK);
            g.drawLine( dotx, doty, dotx+dotside, doty+dotside );
            g.drawLine( dotx+dotside, doty, dotx, doty+dotside );
            g.setColor(oc);
        }
    if (false) {
        /* draw background for strings. */
        g.setColor( Color.WHITE );
        g.fillRect( centrex - 15, centrey - 25, 50, 50 );

        /* draw strings */
        g.setColor( Color.BLACK );
        g.drawString( x+", "+y, centrex-15, centrey-12 );
        String s = (new Double(Globals.gameBoard.voltage[x+1][y+1])).toString();
        g.drawString( s.substring(0, (s.length()<8?s.length():7)),
                      centrex-15, centrey+0  );
        //g.drawString( s, centrex, centrey+0  );
        /* blue voltage */
        String bs = (new Double(bluevolt)).toString();
        g.drawString( bs.substring(0, (bs.length()<8?bs.length():7)),
                      centrex-15, centrey+12  );
        /* red voltage */
        String rs = (new Double(redvolt)).toString();
        g.drawString( rs.substring(0, (rs.length()<8?rs.length():7)),
                      centrex-15, centrey+24  );

        /* draw voltage arrows */
        System.out.println("I am "+x+", "+y+", rb:"+Globals.hexCanvas.iRedWidth+","+Globals.hexCanvas.iBlueWidth);
        /* this square is at position x, y.  When indexing the voltages array,
         * index a, b maps to position a+1, b+1. */
        if( x > 0 && y < (Globals.hexCanvas.iBlueWidth-1) ) {
            System.out.println("Grr, "+(x+1)+", "+(y+1)+" : "+(x)+", "+(y+2));
            if( Globals.gameBoard.voltage[x+1][y+1] > Globals.gameBoard.voltage[x][y+2] ) {
                int ox = Globals.hexCanvas.hcCells[x-1][y+1].centrex;
                int oy = Globals.hexCanvas.hcCells[x-1][y+1].centrey + 10;
                g.drawLine( centrex, centrey - 10, ox, oy );
                g.fillOval( ox-3, oy-3, 6, 6 );
            }
        }
        }
    }

    /**
     * The isInside method is used to determine if a point falls inside
     * this hex cell.
     */

    public boolean isInside( int x, int y ) {
        if( x >= bx && x < cx && y >= by && y < fy )
            return true;
        if( x >= ax && y >= by && x < bx && y < ay && (abm*x+abb< y) )
            return true;
        if( x >= cx && y >= cy && x < dx && y < dy && (cdm*x+cdb< y) )
            return true;
        if( x >= ex && y >= dy && x < dx && y < ey && (dem*x+deb>=y) )
            return true;
        if( x >= ax && y >= ay && x < fx && y < fy && (fam*x+fab>=y) )
            return true;

        return false;
    }

    /**
     * getFill is used to query if this hexCell is occupied.
     */

    public boolean getFill() {
        return bFill;
    }

    /**
     * setFill is used to indicate that a player has placed a bead in
     * this cell, the bead will show up the next time the canvas is painted.
     */

    public void setFill( boolean b, Color c ) {
        bFill = b;
        cDotColor = c;
    }

    /**
     * shade() toggles whether the cell background is shaded, this is
     * usually set by clicking the third mouse button.
     */

    public void shade() {
        bShade = !bShade;
    }

    /**
     * x() toggles whether or not an X should be painted over this cell, this
     * is usually set by clicking the second mouse button.
     */

    public void x() {
        bX = !bX;
    }

    public void setDrawAb( boolean b ) {
        bDrawAb = b;
    }

    public void setDrawBc( boolean b ) {
        bDrawBc = b;
    }

    public void setDrawCd( boolean b ) {
        bDrawCd = b;
    }

}



class DPoint {
    double x, y;
}
