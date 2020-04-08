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

package edu.pdx.imagej.reconstruction.result;

import edu.pdx.imagej.dynamic_parameters.*;

class ResultParameter extends HoldingParameter<ResultOptions> {
    public ResultParameter() {super("Result");}
    @Override
    public void initialize()
    {
        M_amplitude = addParameter(new BoolParameter("Amplitude", false));
        M_phase = addParameter(new BoolParameter("Phase", false));
        M_real = addParameter(new BoolParameter("Real", false));
        M_imaginary = addParameter(new BoolParameter("Imaginary", false));
        String[] choices = {"8-bit", "16-bit", "32-bit"};
        M_resultType = addParameter(new ChoiceParameter("Output Image Type", choices));
        M_save = addParameter(new SaveParameter());
    }
    @Override
    public ResultOptions getValue()
    {
        ResultOptions result = new ResultOptions();
        result.amplitude = M_amplitude.getValue();
        result.phase = M_phase.getValue();
        result.real = M_real.getValue();
        result.imaginary = M_imaginary.getValue();
        String type = M_resultType.getValue();
        if (type.equals("8-bit")) {
            result.type = ResultOptions.Type.Type8Bit;
        }
        else if (type.equals("16-bit")) {
            result.type = ResultOptions.Type.Type16Bit;
        }
        else if (type.equals("32-bit")) {
            result.type = ResultOptions.Type.Type32Bit;
        }
        else {
            throw new RuntimeException(
                "Internal error when trying to read the output type.  The "
                + "output type received was " + type + "."
            );
        }
        result.saveToFile = M_save.getValue();
        result.saveDirectory = M_save.getDirectory();
        String dirStructure = M_save.getDirStructure();
        if (dirStructure.equals("z/t.tiff")) {
            result.dirStructure = ResultOptions.DirStructure.ZT;
        }
        else if (dirStructure.equals("t/z.tiff")) {
            result.dirStructure = ResultOptions.DirStructure.TZ;
        }
        else if (dirStructure.equals("t.tiff")) {
            result.dirStructure = ResultOptions.DirStructure.T;
        }
        else if (dirStructure.equals("z.tiff")) {
            result.dirStructure = ResultOptions.DirStructure.Z;
        }
        else {
            throw new RuntimeException(
                "Internal error when trying to read the directory structure.  "
                + "The directory structure received was " + dirStructure + "."
            );
        }
        return result;
    }

    private BoolParameter   M_amplitude;
    private BoolParameter   M_phase;
    private BoolParameter   M_real;
    private BoolParameter   M_imaginary;
    private ChoiceParameter M_resultType;
    private SaveParameter   M_save;
}
