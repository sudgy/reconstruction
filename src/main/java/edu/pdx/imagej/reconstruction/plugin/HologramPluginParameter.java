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

package edu.pdx.imagej.reconstruction.plugin;

import edu.pdx.imagej.dynamic_parameters.ImageParameter;

/** A parameter that can see what hologram is being selected.  This isn't
 * actually a DParameter, but if any parameter inherits this interface, it will
 * be given a reference to the ImageParameter that is being used to select the
 * hologram.
 */
public interface HologramPluginParameter {
    /** Set the hologram parameter.
     *
     * @param hologram The hologram parameter.
     */
    public void setHologram(ImageParameter hologram);
}
