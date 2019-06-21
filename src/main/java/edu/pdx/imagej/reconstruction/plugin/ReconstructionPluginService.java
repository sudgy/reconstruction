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

import java.util.ArrayList;
import java.util.List;

import org.scijava.InstantiableException;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.Service;
import net.imagej.ImageJService;

/** A service for {@link ReconstructionPlugin}s.
 */
public interface ReconstructionPluginService extends ImageJService {
    /** Get all enabled {@link MainReconstructionPlugin}s.  If this method finds
     * any {@link ReconstructionPlugin} that is not a {@link
     * MainReconstructionPlugin} or {@link SubReconstructionPlugin}, it will
     * throw an exception.
     *
     * @return An instance of all <code>MainReconstructionPlugin</code>s.
     */
    List<ReconstructionPlugin> get_plugins();
    /** Get all {@link ReconstructionPlugin}s of a certain type.  Note that this
     * will get all plugins, even if they are disabled.
     *
     * @param type The type of plugins to get.
     * @return An instance of all plugins of type <code>type</code>.
     */
    <T extends ReconstructionPlugin> List<T> get_plugins(Class<T> type);
    /** Check if a plugin is enabled.
     *
     * @param plugin The plugin to check if it is enabled.
     * @return Whether or not the plugin is enabled.
     */
    boolean is_enabled(Class<? extends ReconstructionPlugin> plugin);
    /** Enable a plugin.
     *
     * @param plugin The plugin to enable.
     */
    void enable(Class<? extends ReconstructionPlugin> plugin);
    /** Disable a plugin.
     *
     * @param plugin The plugin to disable.
     */
    void disable(Class<? extends ReconstructionPlugin> plugin);
}
