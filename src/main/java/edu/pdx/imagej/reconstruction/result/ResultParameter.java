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
        M_amplitude = add_parameter(BoolParameter.class, "Amplitude", false);
        M_phase = add_parameter(BoolParameter.class, "Phase", false);
        M_real = add_parameter(BoolParameter.class, "Real", false);
        M_imaginary = add_parameter(BoolParameter.class, "Imaginary", false);
        String[] choices = {"8-bit", "16-bit", "32-bit"};
        M_result_type = add_parameter(ChoiceParameter.class, "Output Image Type", choices);
        M_save = add_parameter(SaveParameter.class);
    }
    @Override
    public ResultOptions get_value()
    {
        ResultOptions result = new ResultOptions();
        result.amplitude = M_amplitude.get_value();
        result.phase = M_phase.get_value();
        result.real = M_real.get_value();
        result.imaginary = M_imaginary.get_value();
        String type = M_result_type.get_value();
        if (type.equals("8-bit")) {
            result.type = ResultOptions.Type.Type8Bit;
        }
        else if (type.equals("16-bit")) {
            result.type = ResultOptions.Type.Type16Bit;
        }
        else {
            result.type = ResultOptions.Type.Type32Bit;
        }
        result.save_to_file = M_save.get_value();
        result.save_directory = M_save.get_directory();
        return result;
    }

    private BoolParameter   M_amplitude;
    private BoolParameter   M_phase;
    private BoolParameter   M_real;
    private BoolParameter   M_imaginary;
    private ChoiceParameter M_result_type;
    private SaveParameter   M_save;
}
