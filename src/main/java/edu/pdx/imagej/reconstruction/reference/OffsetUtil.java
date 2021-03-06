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
    public static int getOffset(int offset, int current, int min, int max)
    {
        int result = current + offset;
        return result < min ? (min - current) :
               result > max ? (max - current) : offset;
    }

    public static int getMultiOffset(int offset, int current,
                                       int minOut, int maxOut,
                                       int minIn, int maxIn)
    {
        int resultMin = current + offset + minIn - 1;
        int resultMax = current + offset + maxIn - 1;
        if (resultMin < minOut) offset = minOut - minIn - current + 1;
        else if (resultMax > maxOut) offset = maxOut - maxIn - current + 1;
        return offset;
    }
}
