/* Copyright (C) 2019 Portland State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * For any questions regarding the license, please contact the Free Software
 * Foundation.  For any other questions regarding this program, please contact
 * David Cohoe at dcohoe@pdx.edu.
 */

package edu.pdx.imagej.reconstruction;

import java.awt.Point;

public class CenterOptions {
    public CenterOptions(boolean do_center, int degree, Iterable<Point> h_line, Iterable<Point> v_line)
    {
        M_do_center = do_center;
        M_degree = degree;
        M_h_line = h_line;
        M_v_line = v_line;
    }
    public boolean         do_center() {return M_do_center;}
    public int             degree()    {return M_degree;}
    public Iterable<Point> h_line()    {return M_h_line;}
    public Iterable<Point> v_line()    {return M_v_line;}
    private boolean         M_do_center;
    private int             M_degree;
    private Iterable<Point> M_h_line;
    private Iterable<Point> M_v_line;
}
