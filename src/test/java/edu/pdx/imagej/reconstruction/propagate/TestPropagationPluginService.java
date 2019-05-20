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

package edu.pdx.imagej.reconstruction.propagation;

import java.util.LinkedHashMap;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;

@Plugin(type = PropagationPluginService.class, priority = Priority.FIRST)
public class TestPropagationPluginService extends AbstractService
                                          implements PropagationPluginService {
    @Override
    public LinkedHashMap<String, PropagationPlugin> get_plugins()
    {
        return M_plugins;
    }
    public LinkedHashMap<String, PropagationPlugin> M_plugins
        = new LinkedHashMap<>();
}
