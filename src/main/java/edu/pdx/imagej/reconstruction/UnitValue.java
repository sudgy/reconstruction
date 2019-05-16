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

public class UnitValue {
    public UnitValue(double value, Units unit)
    {
        M_value = value;
        M_unit = unit;
    }
    public double as_nano()
    {
        return Units.convert(M_value, M_unit, Units.Nano);
    }
    public double as_micro()
    {
        return Units.convert(M_value, M_unit, Units.Micro);
    }
    public double as_milli()
    {
        return Units.convert(M_value, M_unit, Units.Milli);
    }
    public double as_centi()
    {
        return Units.convert(M_value, M_unit, Units.Centi);
    }
    public double as_meter()
    {
        return Units.convert(M_value, M_unit, Units.Meter);
    }
    private double M_value;
    private Units M_unit;
}
