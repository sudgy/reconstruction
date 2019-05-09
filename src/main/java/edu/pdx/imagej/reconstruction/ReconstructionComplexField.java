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

class ReconstructionComplexField implements ComplexField {
    public ReconstructionComplexField(double[][] field,
                                      ReconstructionFieldImpl containing)
    {
        M_containing = containing;
        M_field = field;
    }
    public void shift()
    {
        field_changed();
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
    @Override public double[][] get_field()
    {
        field_changed();
        return M_field;
    }
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
    @Override public double get_real(int x, int y) {return M_field[x][y*2];}
    @Override public double get_imag(int x, int y) {return M_field[x][y*2+1];}
    @Override public int width()  {return M_field.length;}
    @Override public int height() {return M_field[0].length / 2;}

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

    private void field_changed()
    {
        if (M_containing != null) M_containing.field_changed(this);
    }
    private ReconstructionComplexField() {}

    double[][] M_field;
    private ReconstructionFieldImpl M_containing;
}
