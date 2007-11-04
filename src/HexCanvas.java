
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * HexCanvas displays all the hex components.  The board, the cells,
 * shaded cells X'ed cells and cells number.
 * HexCanvas supports ANY size canvas, even irregular sized boards.
 * <p>
 * The current implementation flickers a lot when it is redraw.
 * A future implementation will only redraw the affected sections
 * of the Canvas.
 * <p>
 * This is how cells are addresses on the hex board.
 * <pre>
 *
 *             *---*
 *            /     \
 *           /       \
 *      *---*   01    *---*
 *     /     \       /     \
 *    /       \     /       \
 *   *   00    *---*   11    *
 *    \       /     \       /
 *   0 \     /       \     / 1
 *      *---*   10    *---*
 *           \       /
 *          1 \     / 0
 *  Red Side   *---*     Blue Side
 *
 * </pre>
 */

public class HexCanvas extends JComponent
	implements MouseListener
{

	int iRedWidth, iBlueWidth;
	boolean bReset;

	boolean p;

	int iWidth = 0, iHeight = 0;
	int iCentreX, iCentreY;
	int iMargin = 20;  /* ten pixel iMargin on all 4 sides. */
	int iLeftSide, iRightSide, iTopSide, iBottomSide;

	int[][] xcoords, ycoords;
	HexCell[][] hcCells = null;

	int[] r1xpoints, r1ypoints;
	int[] r2xpoints, r2ypoints;
	int[] b1xpoints, b1ypoints;
	int[] b2xpoints, b2ypoints;

	/**
	 * constructs a new x by y HexCanvas.
	 */

	public HexCanvas( int x, int y )
	{
		addMouseListener( this );
		setDoubleBuffered( true );

		reset( x, y );
	}

	/**
	 * reset the HexCanvas to new dimensions r by b.
	 */

	public void reset( int r, int b ) {
		iRedWidth = r;
		iBlueWidth = b;
		bReset = true;
		p = true;

		// make cells array
		hcCells = new HexCell[ r ][ b ];
		for( int i = 0; i < r; i++ )
			for( int j = 0; j < b; j++ )
				hcCells[i][j] = new HexCell(i, j);

		r1xpoints = new int[4*iRedWidth+3];
		r1ypoints = new int[4*iRedWidth+3];
		r2xpoints = new int[4*iRedWidth+3];
		r2ypoints = new int[4*iRedWidth+3];
		b1xpoints = new int[4*iBlueWidth+3];
		b1ypoints = new int[4*iBlueWidth+3];
		b2xpoints = new int[4*iBlueWidth+3];
		b2ypoints = new int[4*iBlueWidth+3];

		repaint();
	}

	private void resizeScreen(Graphics g)
	{
		double dAlpha;
		double dVInc, dHInc;
		double dCosTheta, dTanTheta;
		int iVertLines, iHorizLines;

//		if( iWidth < iHeight )
//			iHeight = iWidth;
//		else
//			iWidth = iHeight;

		iLeftSide = iTopSide = iMargin;
		iRightSide = iWidth - iMargin;
		iBottomSide = iHeight - iMargin;
		iCentreX = iWidth / 2;
		iCentreY = iHeight / 2;

		// calc params.
		dCosTheta = Math.cos( Math.PI / 3. );
		dTanTheta = Math.tan( Math.PI / 3. );
		double rb = ((double)iRedWidth) + ((double)iBlueWidth) - 1.;
		dAlpha = (((double)(iRightSide - iLeftSide)) * dCosTheta)
			/ ( ((double)iRedWidth*dCosTheta) + ((double)iBlueWidth*dCosTheta) + rb);
		dVInc = ( ((double)iRightSide)-((double)dAlpha)-((double)iLeftSide) ) / rb;
		dHInc = dAlpha * dTanTheta;

		// make coordinate matrix.
		int h = iRedWidth + iBlueWidth;
		xcoords = new int[2*h][h+1];
		ycoords = new int[2*h][h+1];

		// fill in the coordinate matrix.
		iVertLines = iRedWidth + iBlueWidth;
		iHorizLines = iRedWidth + iBlueWidth + 1;

		double i;
		int ix, ix2;

		g.setColor( Color.RED );

		for( ix = 0, i = iLeftSide; ix < iVertLines; ix++, i += dVInc ) {
//			g.drawLine( (int)Math.round(i), iTopSide, (int)Math.round(i), iBottomSide );
//			g.drawLine( (int)Math.round(i+dAlpha), iTopSide, (int)Math.round(i+dAlpha), iBottomSide );
			for( ix2 = 0; ix2 < iHorizLines; ix2++ ) {
				xcoords[2*ix][ix2]   = (int)Math.round(i);
				xcoords[2*ix+1][ix2] = (int)Math.round(i+dAlpha);
			}
		}

		// draw horizontal lines.
		for( ix = 0, i = iTopSide; ix < iHorizLines; ix++, i += dHInc ) {
//			g.drawLine( iLeftSide, (int)Math.round(i), iRightSide, (int)Math.round(i) );
			for( ix2 = 0; ix2 < 2*iVertLines; ix2++ )
				ycoords[ix2][ix] = (int)Math.round(i);
		}

		//Print out the coord matrix
//		for(ix = 0; ix < iHorizLines; ix++ ) {
//			for(ix2 = 0; ix2 < 2*iVertLines; ix2++ ) {
//				System.out.print( "("+xcoords[ix2][ix]+", "+ycoords[ix2][ix]+") " );
//			}
//			System.out.println("");
//		}

		int xl, yl, fxl, fyl;

		xl = 0;
		yl = iBlueWidth;

		for(ix = 0; ix < iRedWidth; ix++) {
			// Make all cells in the red sides ix th row.

			fxl = xl; fyl = yl;
			
			for(ix2 = 0; ix2 < iBlueWidth; ix2++) {
				// Make all cells in the blue sides ix2 th row.

				hcCells[ix][ix2].setPoints( xcoords[xl  ][yl  ], ycoords[xl  ][yl  ],  // a point
						                    xcoords[xl+1][yl-1], ycoords[xl+1][yl-1],  // b point
						                    xcoords[xl+2][yl-1], ycoords[xl+2][yl-1],  // c point
						                    xcoords[xl+3][yl  ], ycoords[xl+3][yl  ],  // d point
						                    xcoords[xl+2][yl+1], ycoords[xl+2][yl+1],  // e point
						                    xcoords[xl+1][yl+1], ycoords[xl+1][yl+1] );// f point
				if( ix == 0 ) {
					hcCells[ix][ix2].setDrawAb( false );
					hcCells[ix][ix2].setDrawBc( false );
				}
				if( ix2 == (iBlueWidth-1) ) {
					hcCells[ix][ix2].setDrawBc( false );
					hcCells[ix][ix2].setDrawCd( false );
				}

				xl = xl + 2; yl = yl - 1;

			}
			// next
			xl = fxl + 2;
			yl = fyl + 1;

		}

		// make border coords.

		int rn = 2 * iRedWidth;
		int bn = 2 * iBlueWidth;

		int bw = ( (int)Math.round(dAlpha) ) / 3;
		if( bw < 3 )
		    bw = 3;

		int sx, sy;

		/* -- START -- */
		sx = 0;
		sy = iBlueWidth;
		for( ix = 0; ix < rn; ix++ ) {
			r1xpoints[ix] = (int)xcoords[sx][sy];
			r1ypoints[ix] = (int)ycoords[sx][sy];
			if( ix%2 == 0 ) {
				sy++;
			}
			sx++;
		}
		r1xpoints[ix] = (int)(xcoords[sx][sy]+xcoords[sx-1][sy])/2;
		r1ypoints[ix++] = (int)ycoords[sx][sy];
		r1xpoints[ix] = (int)(xcoords[sx][sy]+xcoords[sx-1][sy])/2;
		r1ypoints[ix++] = (int)ycoords[sx][sy]+bw;
		for( ix2 = ix-3; ix2 >= 0; ix2--,ix++ ) {
			if( ix2%2 == 0 ) {
				sy--;
			}
			sx--;
			r1xpoints[ix] = (int)xcoords[sx][sy] - bw;
			r1ypoints[ix] = (int)ycoords[sx][sy] + bw;
		}
		r1xpoints[ix] = (int)xcoords[0][iBlueWidth]-bw;
		r1ypoints[ix] = (int)ycoords[0][iBlueWidth];

		sx = 2*(iRedWidth+iBlueWidth)-1;
		sy = iRedWidth;
		for( ix = 0; ix < rn; ix++ ) {
			r2xpoints[ix] = (int)xcoords[sx][sy];
			r2ypoints[ix] = (int)ycoords[sx][sy];
			if( ix%2 == 0 ) {
				sy--;
			}
			sx--;
		}
		r2xpoints[ix] = (int)(xcoords[sx][sy]+xcoords[sx+1][sy])/2;
		r2ypoints[ix++] = (int)ycoords[sx][sy];
		r2xpoints[ix] = (int)(xcoords[sx][sy]+xcoords[sx+1][sy])/2;
		r2ypoints[ix++] = (int)ycoords[sx][sy]-bw;
		for( ix2 = ix-3; ix2 >= 0; ix2--,ix++ ) {
			if( ix2%2 == 0 ) {
				sy++;
			}
			sx++;
			r2xpoints[ix] = (int)xcoords[sx][sy] + bw;
			r2ypoints[ix] = (int)ycoords[sx][sy] - bw;
		}
		r2xpoints[ix] = (int)xcoords[2*(iRedWidth+iBlueWidth)-1][iRedWidth]+bw;
		r2ypoints[ix] = (int)ycoords[2*(iRedWidth+iBlueWidth)-1][iRedWidth];

		sx = 0;
		sy = iBlueWidth;
		for( ix = 0; ix < bn; ix++ ) {
			b1xpoints[ix] = (int)xcoords[sx][sy];
			b1ypoints[ix] = (int)ycoords[sx][sy];
			if( ix%2 == 0 ) {
				sy--;
			}
			sx++;
		}
		b1xpoints[ix] = (int)(xcoords[sx][sy]+xcoords[sx-1][sy])/2;
		b1ypoints[ix++] = (int)ycoords[sx][sy];
		b1xpoints[ix] = (int)(xcoords[sx][sy]+xcoords[sx-1][sy])/2;
		b1ypoints[ix++] = (int)ycoords[sx][sy]-bw;
		for( ix2 = ix-3; ix2 >= 0; ix2--,ix++ ) {
			if( ix2%2 == 0 ) {
				sy++;
			}
			sx--;
			b1xpoints[ix] = (int)xcoords[sx][sy] - bw;
			b1ypoints[ix] = (int)ycoords[sx][sy] - bw;
		}
		b1xpoints[ix] = (int)xcoords[0][iBlueWidth]-bw;
		b1ypoints[ix] = (int)ycoords[0][iBlueWidth];

		sx = 2*(iRedWidth+iBlueWidth)-1;
		sy = iRedWidth;
		for( ix = 0; ix < bn; ix++ ) {
			b2xpoints[ix] = (int)xcoords[sx][sy];
			b2ypoints[ix] = (int)ycoords[sx][sy];
			if( ix%2 == 0 ) {
				sy++;
			}
			sx--;
		}
		b2xpoints[ix] = (int)(xcoords[sx][sy]+xcoords[sx+1][sy])/2;
		b2ypoints[ix++] = (int)ycoords[sx][sy];
		b2xpoints[ix] = (int)(xcoords[sx][sy]+xcoords[sx+1][sy])/2;
		b2ypoints[ix++] = (int)ycoords[sx][sy]+bw;
		for( ix2 = ix-3; ix2 >= 0; ix2--,ix++ ) {
			if( ix2%2 == 0 ) {
				sy--;
			}
			sx++;
			b2xpoints[ix] = (int)xcoords[sx][sy] + bw;
			b2ypoints[ix] = (int)ycoords[sx][sy] + bw;
		}
		b2xpoints[ix] = (int)xcoords[2*(iRedWidth+iBlueWidth)-1][iRedWidth]+bw;
		b2ypoints[ix] = (int)ycoords[2*(iRedWidth+iBlueWidth)-1][iRedWidth];


	}

	public void paint( Graphics g )
	{
		if( iWidth != (getWidth()-1) || iHeight != (getHeight()-1) || bReset ) {
			bReset = false;
			iWidth = getWidth()-1;
			iHeight = getHeight()-1;
			resizeScreen(g);
			System.out.println("New size: "+iWidth+", "+iHeight);
		}

		// Outside border
//		g.drawLine(0, 0, 0, iHeight );
//		g.drawLine(0, 0, iWidth, 0 );
//		g.drawLine(0, iHeight, iWidth, iHeight );
//		g.drawLine(iWidth, 0, iWidth, iHeight );

		// Margin border
//		g.drawLine( iLeftSide, iTopSide, iLeftSide, iBottomSide );
//		g.drawLine( iLeftSide, iTopSide, iRightSide, iTopSide );
//		g.drawLine( iRightSide, iTopSide, iRightSide, iBottomSide );
//		g.drawLine( iRightSide, iBottomSide, iLeftSide, iBottomSide );

		// draw cells
		
		//g.setColor( Color.GRAY );
		g.setColor( Color.DARK_GRAY );
		//g.setColor( Color.BLACK );

		int ix, ix2;
		for(ix = 0; ix < iRedWidth; ix++) {
			for(ix2 = 0; ix2 < iBlueWidth; ix2++) {
				hcCells[ix][ix2].paint(g);
			}
		}

		// Board borders
		
		g.setColor( Color.RED );
		g.fillPolygon( r1xpoints, r1ypoints, r1xpoints.length );
		g.fillPolygon( r2xpoints, r2ypoints, r2xpoints.length );


		g.setColor( Color.BLUE );
		g.fillPolygon( b1xpoints, b1ypoints, b1xpoints.length );
		g.fillPolygon( b2xpoints, b2ypoints, b2xpoints.length );

	}

	public void update( Graphics g )
	{
		System.out.println("HexCanvas::update() called");
		if( iWidth != (getWidth()-1) || iHeight != (getHeight()-1) || bReset ) {
			bReset = false;
			iWidth = getWidth()-1;
			iHeight = getHeight()-1;
			resizeScreen(g);
			System.out.println("New size: "+iWidth+", "+iHeight);
		}

		// Outside border
//		g.drawLine(0, 0, 0, iHeight );
//		g.drawLine(0, 0, iWidth, 0 );
//		g.drawLine(0, iHeight, iWidth, iHeight );
//		g.drawLine(iWidth, 0, iWidth, iHeight );

		// Margin border
//		g.drawLine( iLeftSide, iTopSide, iLeftSide, iBottomSide );
//		g.drawLine( iLeftSide, iTopSide, iRightSide, iTopSide );
//		g.drawLine( iRightSide, iTopSide, iRightSide, iBottomSide );
//		g.drawLine( iRightSide, iBottomSide, iLeftSide, iBottomSide );

		// draw cells
		
		//g.setColor( Color.GRAY );
		g.setColor( Color.DARK_GRAY );
		//g.setColor( Color.BLACK );

		int ix, ix2;
		for(ix = 0; ix < iRedWidth; ix++) {
			for(ix2 = 0; ix2 < iBlueWidth; ix2++) {
				hcCells[ix][ix2].paint(g);
			}
		}

		// Board borders
		
		g.setColor( Color.RED );
		g.fillPolygon( r1xpoints, r1ypoints, r1xpoints.length );
		g.fillPolygon( r2xpoints, r2ypoints, r2xpoints.length );


		g.setColor( Color.BLUE );
		g.fillPolygon( b1xpoints, b1ypoints, b1xpoints.length );
		g.fillPolygon( b2xpoints, b2ypoints, b2xpoints.length );

	}

	public void mouseClicked( MouseEvent e ) {

		if( e.getButton() == MouseEvent.BUTTON1 )
			return;

		// Find which cell this event happened in.
		for( int ix = 0; ix < iRedWidth; ix++ ) {
			for( int ix2 = 0; ix2 < iBlueWidth; ix2++ ) {
				HexCell h = hcCells[ix][ix2];

				if( h.isInside( e.getX(), e.getY() ) ) {
					if( e.getButton() == MouseEvent.BUTTON2 )
						h.x();
					if( e.getButton() == MouseEvent.BUTTON3 ) {
						//h.shade();
						h.drawArrows = true;
						//System.out.println("Draw arrows for "+ix+", "+ix2+"\n");
					}
					repaint( );
				}
				else {
					h.drawArrows = false;
				}
			}
		}
	}

	public void mouseEntered( MouseEvent e ) {}
	public void mouseExited( MouseEvent e ) {}
	public void mousePressed( MouseEvent e ) {}
	public void mouseReleased( MouseEvent e ) {}

	/**
	 * After a call to markCell, cell (r,b) will be occupied by
	 * player.  If player is 0 then the cell is unmarked.  This is useful
	 * for implementing undo.
	 */

	public void markCell( int r, int b, int player ) {
		/**
		 * @param r r is the red index.
		 * @param b b is the blue index.
		 * @param player player is the player number to mark.
		 *        0 means clear the cell (for undo).
		 *        1 means player number 1 (red player).
		 *        2 means player number 1 (blue player).
		 */

		HexCell h = hcCells[r][b];

		switch( player ) {
		case 1:
			h.setFill( true, Color.RED );
			break;
		case 2:
			h.setFill( true, Color.BLUE );
			break;
		default:
			h.setFill( false, Color.BLACK );
			break;
		}
		//repaint(h.dotx, h.doty, h.dotside, h.dotside );
		repaint();
	}

	/**
	 * xy2HexCell takes an (x,y) point and returns the HexCell the
	 * point is located in.
	 *
	 * @return returns the HexCell that this point falls into.
	 * @param x x is the x location in screen coordinates.
	 * @param y y is the y location in screen coordinates.
	 */

	public HexCell xy2HexCell( int x, int y ) {

		// Find which cell this event happened in.
		for( int ix = 0; ix < iRedWidth; ix++ ) {
			for( int ix2 = 0; ix2 < iBlueWidth; ix2++ ) {

				if( hcCells[ix][ix2].isInside( x, y ) )
					return hcCells[ix][ix2];
			}
		}
		return null;
	}

	/**
	 * xy2Point takes an (x,y) point and returns the HexCell number
	 * the point is located in.
	 *
	 * @return returns the Point that this (x,y) falls into.
	 * @param x x is the x location in screen coordinates.
	 * @param y y is the y location in screen coordinates.
	 */

	public Point xy2Point( int x, int y ) {

		/**
		 * point2Cell takes an (x,y) point and returns the HexCell the
		 * point is located in.
		 *
		 * @return returns the HexCell that this point falls into.
		 * @param x x is the x location in screen coordinates.
		 * @param y y is the y location in screen coordinates.
		 */

		// Find which cell this event happened in.
		for( int ix = 0; ix < iRedWidth; ix++ ) {
			for( int ix2 = 0; ix2 < iBlueWidth; ix2++ ) {

				if( hcCells[ix][ix2].isInside( x, y ) )
					return new Point(ix, ix2);
			}
		}
		return null;
	}

	public Dimension getMinimumSize() {
		System.out.println("getMinSize called");
		return new Dimension( 320, 200 );
	}
}


