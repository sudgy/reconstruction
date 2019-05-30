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

package edu.pdx.imagej.reconstruction.reference;

class OffsetUtil {
    public static int get_offset(int offset, int current, int min, int max)
    {
        int result = current + offset;
        return result < min ? (min - current) :
               result > max ? (max - current) : offset;
    }

    public static int get_multi_offset(int offset, int current,
                                       int min_out, int max_out,
                                       int min_in, int max_in)
    {
        int result_min = current + offset + min_in - 1;
        int result_max = current + offset + max_in - 1;
        if (result_min < min_out) offset = min_out - min_in - current + 1;
        else if (result_max > max_out) offset = max_out - max_in - current + 1;
        return offset;
    }
}
