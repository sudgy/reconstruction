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

package edu.pdx.imagej.reconstruction.units;

public class DistanceUnitValue {
    public DistanceUnitValue(double value, DistanceUnits unit)
    {
        M_value = value;
        M_unit = unit;
    }
    public double as_nano()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Nano);
    }
    public double as_micro()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Micro);
    }
    public double as_milli()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Milli);
    }
    public double as_centi()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Centi);
    }
    public double as_meter()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Meter);
    }
    private double M_value;
    private DistanceUnits M_unit;
}
