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

/** This is the default implementation of {@link ComplexField}.
 */
public class ReconstructionComplexField implements ComplexField {
    /** Constructor with the field and a (possibly null) {@link
     * ReconstructionFieldImpl} that this field is in.
     *
     * @param field The complex data for this field.  It must be size
     *              [width][height*2] where the real value at (x, y) is at
     *              [x][y*2] and the imaginary value is at [x][y*2+1].
     * @param containing The {@link ReconstructionFieldImpl} that this field is
     *                   in.  It can be <code>null</code> if this field is not
     *                   associated with any ReconstructionField.
     */
    public ReconstructionComplexField(double[][] field,
                                      ReconstructionFieldImpl containing)
    {
        M_containing = containing;
        M_field = field;
    }
    void set_containing(ReconstructionFieldImpl containing)
    {
        M_containing = containing;
    }
    /** Perform a shift of the data.  As an example, when the width and height
     * are even, the data can be represented as four equally-sized pieces:<br>
     * <code>a b<br>
     *       c d</code><br>
     * Once shifted, the data will look like<br>
     * <code>a b<br>
     *       c d</code>.<br>
     *
     * The reason there is a <code>shift_forward()</code> and a {@link
     * #shift_backward shift_backward()} method is that when the array is odd
     * the above diagram does not work perfectly, and the forward shift and the
     * backward shift no longer represent the same operation.
     *
     * @see shift_backward
     */
    public void shift_forward()
    {
        field_changed();
        int w = width();
        int h = height();
        // If both sides are even, the algorithm can be done in place with a
        // single temporary value.  If not, several values rotate, and copying
        // the whole thing is simpler.
        if (w % 2 == 0 && h % 2 == 0) shift_even();
        else shift_odd(true);
    }
    /** Perform a shift of the data.  As an example, when the width and height
     * are even, the data can be represented as four equally-sized pieces:<br>
     * <code>a b<br>
     *       c d</code><br>
     * Once shifted, the data will look like<br>
     * <code>a b<br>
     *       c d</code>.<br>
     *
     * The reason there is a {@link #shift_forward shift_forward()} and a
     * <code>shift_backward()</code> method is that when the array is odd the
     * above diagram does not work perfectly, and the forward shift and the
     * backward shift no longer represent the same operation.
     *
     * @see shift_forward
     */
    public void shift_backward()
    {
        field_changed();
        int w = width();
        int h = height();
        if (w % 2 == 0 && h % 2 == 0) shift_even();
        else shift_odd(false);
    }
    // Perform the shift when it is even
    private void shift_even()
    {
        int w = width();
        int h = height();
        int w2 = w / 2;
        int h2 = h / 2;
        double tmp;
        for (int i = 0; i < w2; i++) {
            for (int j = 0; j < h2; j++) {
                int x1 = i;
                int x2 = i + w2;
                int y1r = 2 * j;
                int y1i = y1r + 1;
                int y2r = 2 * (j + h2);
                int y2i = y2r + 1;

                tmp              = M_field[x1][y1r];
                M_field[x1][y1r] = M_field[x2][y2r];
                M_field[x2][y2r] = tmp;
                tmp              = M_field[x1][y1i];
                M_field[x1][y1i] = M_field[x2][y2i];
                M_field[x2][y2i] = tmp;

                tmp              = M_field[x2][y1r];
                M_field[x2][y1r] = M_field[x1][y2r];
                M_field[x1][y2r] = tmp;
                tmp              = M_field[x2][y1i];
                M_field[x2][y1i] = M_field[x1][y2i];
                M_field[x1][y2i] = tmp;
            }
        }
    }
    // Perform the shift when it is odd
    private void shift_odd(boolean forward)
    {
        int w = width();
        int h = height();
        boolean width_even = w % 2 == 0;
        boolean height_even = h % 2 == 0;
        int w2 = w / 2;
        int h2 = h / 2;
        int wp = width_even ? 0 : 1;
        int hp = height_even ? 0 : 1;
        double[][] new_field = new double[w][h * 2];
        for (int i = 0; i < w2 + wp; ++i) {
            for (int j = 0; j < h2 + hp; ++j) {
                // Coord one is on the top left
                // Coord two is the closer one on the bottom right
                // Coord three is the further one on the bottom right
                int x1 = i;
                int y1r = 2 * j;
                int y1i = y1r + 1;
                int x2 = x1 + w2;
                int x3 = x2 + wp;
                int y2r = 2 * (j + h2);
                int y2i = y2r + 1;
                int y3r = 2 * (j + h2 + hp);
                int y3i = y3r + 1;

                // Top left
                if (forward || (i != w2 && j != h2)) {
                    if (forward) {
                        new_field[x1][y1r] = M_field[x2][y2r];
                        new_field[x1][y1i] = M_field[x2][y2i];
                    }
                    else {
                        new_field[x1][y1r] = M_field[x3][y3r];
                        new_field[x1][y1i] = M_field[x3][y3i];
                    }
                }
                // Top right
                if ((forward && i != w2) || (!forward && j != h2)) {
                    if (forward) {
                        new_field[x3][y1r] = M_field[x1][y2r];
                        new_field[x3][y1i] = M_field[x1][y2i];
                    }
                    else {
                        new_field[x2][y1r] = M_field[x1][y3r];
                        new_field[x2][y1i] = M_field[x1][y3i];
                    }
                }
                // Bottom left
                if ((forward && j != h2) || (!forward && i != w2)) {
                    if (forward) {
                        new_field[x1][y3r] = M_field[x2][y1r];
                        new_field[x1][y3i] = M_field[x2][y1i];
                    }
                    else {
                        new_field[x1][y2r] = M_field[x3][y1r];
                        new_field[x1][y2i] = M_field[x3][y1i];
                    }
                }
                // Bottom right
                if (!forward || (i != w2 && j != h2)) {
                    if (forward) {
                        new_field[x3][y3r] = M_field[x1][y1r];
                        new_field[x3][y3i] = M_field[x1][y1i];
                    }
                    else {
                        new_field[x2][y2r] = M_field[x1][y1r];
                        new_field[x2][y2i] = M_field[x1][y1i];
                    }
                }
            }
        }
        M_field = new_field;
    }

    /** {@inheritDoc} */
    @Override public ReconstructionComplexField copy()
    {
        ReconstructionComplexField result = new ReconstructionComplexField();
        int w = width();
        int h = height();
        result.M_field = new double[w][h * 2];
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h * 2; ++y) {
                result.M_field[x][y] = M_field[x][y];
            }
        }
        return result;
    }
    // Copy, but make containing set
    ReconstructionComplexField copy_in(ReconstructionFieldImpl containing)
    {
        ReconstructionComplexField result = copy();
        result.M_containing = containing;
        return result;
    }
    /** {@inheritDoc} */
    @Override public double[][] get_field()
    {
        field_changed();
        return M_field;
    }
    /** {@inheritDoc} */
    @Override public void set_field(double[][] field)
    {
        field_changed();
        M_field = field;
    }
    /** {@inheritDoc} */
    @Override public double[][] get_real()
    {
        int w = width();
        int h = height();
        double[][] result = new double[w][h];
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                result[x][y] = M_field[x][2 * y];
            }
        }
        return result;
    }
    /** {@inheritDoc} */
    @Override public double[][] get_imag()
    {
        int w = width();
        int h = height();
        double[][] result = new double[w][h];
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                result[x][y] = M_field[x][2 * y + 1];
            }
        }
        return result;
    }
    /** {@inheritDoc} */
    @Override public double[][] get_amp()
    {
        int w = width();
        int h = height();
        double[][] result = new double[w][h];
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                double r = M_field[x][2 * y];
                double i = M_field[x][2 * y + 1];
                result[x][y] = Math.sqrt(r*r + i*i);
            }
        }
        return result;
    }
    /** {@inheritDoc} */
    @Override public double[][] get_amp2()
    {
        int w = width();
        int h = height();
        double[][] result = new double[w][h];
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                double r = M_field[x][2 * y];
                double i = M_field[x][2 * y + 1];
                result[x][y] = r*r + i*i;
            }
        }
        return result;
    }
    /** {@inheritDoc} */
    @Override public double[][] get_arg()
    {
        int w = width();
        int h = height();
        double[][] result = new double[w][h];
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                double r = M_field[x][2 * y];
                double i = M_field[x][2 * y + 1];
                result[x][y] = Math.atan2(i, r);
            }
        }
        return result;
    }
    /** {@inheritDoc} */
    @Override public double get_real(int x, int y) {return M_field[x][y*2];}
    /** {@inheritDoc} */
    @Override public double get_imag(int x, int y) {return M_field[x][y*2+1];}
    /** {@inheritDoc} */
    @Override public int width()  {return M_field.length;}
    /** {@inheritDoc} */
    @Override public int height() {return M_field[0].length / 2;}

    /** {@inheritDoc} */
    @Override public void negate_in_place()
    {
        field_changed();
        int w = width();
        int h = height();
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h * 2; ++y) {
                M_field[x][y] = -M_field[x][y];
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void add_in_place(ComplexField other)
    {
        if (other instanceof ReconstructionComplexField) {
            add_in_place(((ReconstructionComplexField)other).M_field);
        }
        else {
            field_changed();
            int w = width();
            int h = height();
            for (int x = 0; x < w; ++x) {
                for (int y = 0; y < h * 2; ++y) {
                    M_field[x][y*2] += other.get_real(x, y);
                    M_field[x][y*2+1] += other.get_imag(x, y);
                }
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void subtract_in_place(ComplexField other)
    {
        if (other instanceof ReconstructionComplexField) {
            subtract_in_place(((ReconstructionComplexField)other).M_field);
        }
        else {
            field_changed();
            int w = width();
            int h = height();
            for (int x = 0; x < w; ++x) {
                for (int y = 0; y < h * 2; ++y) {
                    M_field[x][y*2] -= other.get_real(x, y);
                    M_field[x][y*2+1] -= other.get_imag(x, y);
                }
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void multiply_in_place(ComplexField other)
    {
        if (other instanceof ReconstructionComplexField) {
            multiply_in_place(((ReconstructionComplexField)other).M_field);
        }
        else {
            field_changed();
            int w = width();
            int h = height();
            for (int x = 0; x < w; ++x) {
                for (int y = 0; y < h * 2; ++y) {
                    double a = M_field[x][y*2];
                    double b = M_field[x][y*2+1];
                    double c = other.get_real(x, y);
                    double d = other.get_imag(x, y);
                    M_field[x][y*2] = a * c - b * d;
                    M_field[x][y*2+1] = a * d + b * c;
                }
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void divide_in_place(ComplexField other)
    {
        if (other instanceof ReconstructionComplexField) {
            divide_in_place(((ReconstructionComplexField)other).M_field);
        }
        else {
            field_changed();
            int w = width();
            int h = height();
            for (int x = 0; x < w; ++x) {
                for (int y = 0; y < h * 2; ++y) {
                    double a = M_field[x][y*2];
                    double b = M_field[x][y*2+1];
                    double c = other.get_real(x, y);
                    double d = other.get_imag(x, y);
                    double denom = c*c + d*d;
                    M_field[x][y*2] = (a * c + b * d) / denom;
                    M_field[x][y*2+1] = (b * c - a * d) / denom;
                }
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void add_in_place(double[][] other)
    {
        field_changed();
        int w = width();
        int h = height() * 2;
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                M_field[x][y] += other[x][y];
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void subtract_in_place(double[][] other)
    {
        field_changed();
        int w = width();
        int h = height() * 2;
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                M_field[x][y] -= other[x][y];
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void multiply_in_place(double[][] other)
    {
        field_changed();
        int w = width();
        int h = height();
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                double a = M_field[x][y*2];
                double b = M_field[x][y*2+1];
                double c = other[x][2*y];
                double d = other[x][2*y+1];
                M_field[x][y*2] = a * c - b * d;
                M_field[x][y*2+1] = a * d + b * c;
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void divide_in_place(double[][] other)
    {
        field_changed();
        int w = width();
        int h = height();
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                double a = M_field[x][y*2];
                double b = M_field[x][y*2+1];
                double c = other[x][2*y];
                double d = other[x][2*y+1];
                double denom = c*c + d*d;
                M_field[x][y*2] = (a * c + b * d) / denom;
                M_field[x][y*2+1] = (b * c - a * d) / denom;
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void add_in_place(double real, double imag)
    {
        field_changed();
        int w = width();
        int h = height();
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                M_field[x][2*y] += real;
                M_field[x][2*y+1] += imag;
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void subtract_in_place(double real, double imag)
    {
        add_in_place(-real, -imag);
    }
    /** {@inheritDoc} */
    @Override public void multiply_in_place(double real, double imag)
    {
        field_changed();
        int w = width();
        int h = height();
        double c = real;
        double d = imag;
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                double a = M_field[x][y*2];
                double b = M_field[x][y*2+1];
                M_field[x][y*2] = a * c - b * d;
                M_field[x][y*2+1] = a * d + b * c;
            }
        }
    }
    /** {@inheritDoc} */
    @Override public void divide_in_place(double real, double imag)
    {
        field_changed();
        int w = width();
        int h = height();
        double c = real;
        double d = imag;
        double denom = c*c + d*d;
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                double a = M_field[x][y*2];
                double b = M_field[x][y*2+1];
                M_field[x][y*2] = (a * c + b * d) / denom;
                M_field[x][y*2+1] = (b * c - a * d) / denom;
            }
        }
    }

    private void field_changed()
    {
        if (M_containing != null) {
            M_containing.field_changed(this);
        }
    }
    private ReconstructionComplexField() {}

    double[][] M_field;
    private ReconstructionFieldImpl M_containing;
}
