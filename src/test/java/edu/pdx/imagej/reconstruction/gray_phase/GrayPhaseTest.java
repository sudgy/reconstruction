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

package edu.pdx.imagej.reconstruction.gray_phase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;

public class GrayPhaseTest {
    @Test public void testGray()
    {
        GrayPhase test = new GrayPhase(true);
        double[][] real = {
            {2, 1},
            {5, 6}
        };
        double[][] imag = {
            {0, 0},
            {0, 0}
        };
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        test.processPropagatedField(field, 1, null);
        double[][] phase = field.field().getArg();
        assertEquals(0, phase[0][0], 0.1);
        assertEquals(0, phase[0][1], 0.1);
        assertEquals(0, phase[1][0], 0.1);
        assertEquals(0, phase[1][1], 0.1);
    }
    @Test public void testConstantPos()
    {
        GrayPhase test = new GrayPhase(true);
        double[][] real = {
            {0, 0},
            {0, 0}
        };
        double[][] imag = {
            {1, 2},
            {3, 4}
        };
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        test.processPropagatedField(field, 1, null);
        double[][] phase = field.field().getArg();
        assertEquals(0, phase[0][0], 0.1);
        assertEquals(0, phase[0][1], 0.1);
        assertEquals(0, phase[1][0], 0.1);
        assertEquals(0, phase[1][1], 0.1);
    }
    @Test public void testConstantNeg()
    {
        GrayPhase test = new GrayPhase(true);
        double[][] real = {
            {0, 0},
            {0, 0}
        };
        double[][] imag = {
            {-1, -2},
            {-3, -4}
        };
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        test.processPropagatedField(field, 1, null);
        double[][] phase = field.field().getArg();
        assertEquals(0, phase[0][0], 0.1);
        assertEquals(0, phase[0][1], 0.1);
        assertEquals(0, phase[1][0], 0.1);
        assertEquals(0, phase[1][1], 0.1);
    }
    @Test public void testPos()
    {
        GrayPhase test = new GrayPhase(true);
        double[][] real = {
            {0.001, 4},
            {-0.001, 0.002}
        };
        double[][] imag = {
            {1, 0},
            {3, 4}
        };
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        test.processPropagatedField(field, 1, null);
        double[][] phase = field.field().getArg();
        assertEquals(0, phase[0][0], 0.1);
        assertEquals(-Math.PI / 2.0, phase[0][1], 0.1);
        assertEquals(0, phase[1][0], 0.1);
        assertEquals(0, phase[1][1], 0.1);
    }
    @Test public void testNeg()
    {
        GrayPhase test = new GrayPhase(true);
        double[][] real = {
            {0.001, 4},
            {-0.001, 0.002}
        };
        double[][] imag = {
            {-1, 0},
            {-3, -4}
        };
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        test.processPropagatedField(field, 1, null);
        double[][] phase = field.field().getArg();
        assertEquals(0, phase[0][0], 0.1);
        assertEquals(Math.PI / 2.0, phase[0][1], 0.1);
        assertEquals(0, phase[1][0], 0.1);
        assertEquals(0, phase[1][1], 0.1);
    }
    @Test public void testWrap()
    {
        GrayPhase test = new GrayPhase(true);
        double[][] real = {
            {-1, -2},
            {-3, -4}
        };
        double[][] imag = {
            { 0.001, -0.001},
            {-0.001,  0.001},
        };
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        test.processPropagatedField(field, 1, null);
        double[][] phase = field.field().getArg();
        assertEquals(0, phase[0][0], 0.1);
        assertEquals(0, phase[0][1], 0.1);
        assertEquals(0, phase[1][0], 0.1);
        assertEquals(0, phase[1][1], 0.1);
    }
}
