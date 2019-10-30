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

package edu.pdx.imagej.reconstruction.poly_tilt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.awt.Point;

import ij.ImagePlus;
import ij.process.FloatProcessor;

public class MiddleTest {
    @Test public void testLines()
    {
        ImagePlus imp = new ImagePlus("", new FloatProcessor(8, 8));
        Middle test = new Middle();
        test.processHologramParam(imp);
        int i = 0;
        for (Point p : test.getHLine()) {
            assertEquals(++i, p.x);
            assertEquals(4, p.y); // In the middle
        }
        assertEquals(6, i, "It should end at 7/8 through the image.");
        i = 0;
        for (Point p : test.getVLine()) {
            assertEquals(4, p.x); // In the middle
            assertEquals(++i, p.y);
        }
        assertEquals(6, i, "It should end at 7/8 through the image.");
    }
    // This should never be done, but we don't want it to crash
    @Test public void testTiny()
    {
        ImagePlus imp = new ImagePlus("", new FloatProcessor(1, 1));
        Middle test = new Middle();
        test.processHologramParam(imp);
        for (Point p : test.getHLine()) {
            assertEquals(0, p.x);
            assertEquals(0, p.y);
        }
        for (Point p : test.getVLine()) {
            assertEquals(0, p.x);
            assertEquals(0, p.y);
        }
    }
}
