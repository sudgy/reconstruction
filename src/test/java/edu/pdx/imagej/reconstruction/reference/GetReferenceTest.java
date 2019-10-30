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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;

public class GetReferenceTest {
    @Test public void testNothing()
    {
        ReconstructionFieldImpl field = makeField();
        Reference test = new Reference(null, false, false);
        test.getReference(field);
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                String coord = "(" + x + ", " + y + ")";
                assertEquals(1, field.field().getReal(x, y), "at " + coord);
                assertEquals(0, field.field().getImag(x, y), "at " + coord);
            }
        }
    }
    @Test public void testPhase()
    {
        ReconstructionFieldImpl field = makeField();
        ReconstructionFieldImpl reference = field.copy();
        Reference test = new Reference(null, true, false);
        test.getReference(reference);
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                double fieldArg = field.field().getArg()[x][y];
                double referenceArg = reference.field().getArg()[x][y];
                double referenceAmp = reference.field().getAmp()[x][y];
                String coord = "(" + x + ", " + y + ")";
                assertEquals(-fieldArg, referenceArg, 1e-6, "at " + coord);
                assertEquals(1, referenceAmp, 1e-6, "at " + coord);
            }
        }
    }
    @Test public void testAmplitude()
    {
        ReconstructionFieldImpl field = makeField();
        ReconstructionFieldImpl reference = field.copy();
        Reference test = new Reference(null, false, true);
        test.getReference(reference);
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                double fieldAmp = field.field().getAmp()[x][y];
                double referenceArg = reference.field().getArg()[x][y];
                double referenceAmp = reference.field().getAmp()[x][y];
                String coord = "(" + x + ", " + y + ")";
                assertEquals(0, referenceArg, "at " + coord);
                // Amplitude reference holo does funny stuff to the oustides
                if (x == 0 || y == 0 || x == 3 || y == 3) continue;
                assertEquals(1, referenceAmp * fieldAmp, 1e-6, "at " + coord
                    + ", fieldAmp was " + fieldAmp + " and referenceAmp was "
                    + referenceAmp + ".");
            }
        }
    }
    @Test public void testBoth()
    {
        ReconstructionFieldImpl field = makeField();
        ReconstructionFieldImpl reference = field.copy();
        Reference test = new Reference(null, true, true);
        test.getReference(reference);
        field.field().multiplyInPlace(reference.field());
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                String coord = "(" + x + ", " + y + ")";
                assertEquals(0, field.field().getImag(x, y), 1e-6,
                             "at " + coord);
                // Amplitude reference holo does funny stuff to the oustides
                if (x == 0 || y == 0 || x == 3 || y == 3) continue;
                assertEquals(1, field.field().getReal(x, y), 1e-6,
                             "at " + coord);
            }
        }
    }

    private static double[][] M_real = new double[][] {
        {0.7491333299, 0.5542820629, 0.1879272540, 0.8584170661},
        {0.0305604090, 0.7808111477, 0.6247602260, 0.6811765293},
        {0.6611121864, 0.3942249921, 0.1238077507, 0.1966343374},
        {0.5457368629, 0.9026601034, 0.7550818323, 0.5276090343}
    };
    private static double[][] M_imag = new double[][] {
        {0.6906533149, 0.1062510323, 0.5731869642, 0.2101399789},
        {0.6916196061, 0.2327204311, 0.9912915487, 0.5350163478},
        {0.2655718145, 0.1526346228, 0.2690232265, 0.7611883011},
        {0.7277631769, 0.6861068860, 0.9135765966, 0.0137632145}
    };
    private static ReconstructionFieldImpl makeField()
    {
        return new ReconstructionFieldImpl(M_real, M_imag);
    }
}
