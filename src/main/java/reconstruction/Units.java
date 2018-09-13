/* Copyright (C) 2018 Portland State University
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

public enum Units {
    Nano, Micro, Milli, Centi, Meter;

    public static Units value_of(String s)
    {
        switch (s) {
            case "Nanometers": return Nano;
            case "Micrometers": return Micro;
            case "Millimeters": return Milli;
            case "Centimeters": return Centi;
            case "Meter": return Meter;
        }
        return null;
    }
    @Override
    public String toString()
    {
        switch (this) {
            case Nano: return "nm";
            case Micro: return "µm";
            case Milli: return "mm";
            case Centi: return "cm";
            case Meter: return "m";
        }
        return null;
    }
}
