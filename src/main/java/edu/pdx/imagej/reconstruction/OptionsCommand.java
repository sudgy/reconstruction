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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.BoolParameter;
import edu.pdx.imagej.dynamic_parameters.ChoiceParameter;
import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.DPDialog;

import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPluginService;
import edu.pdx.imagej.reconstruction.units.DistanceUnits;
import edu.pdx.imagej.reconstruction.units.UnitService;

/** A command that gets and sets the options for reconstruction.  It does all of
 * the options for all of the {@link
 * edu.pdx.imagej.reconstruction.ReconstructionPlugin ReconstructionPlugin}s,
 * along with a couple other things like units.
 */
@Plugin(type = Command.class, menuPath = "Plugins>DHM>Reconstruction Options")
public class OptionsCommand implements Command, Initializable {
    @Override
    public void initialize()
    {
        PluginOptionsParameter.S_all_parameters.clear();
        M_param = new OptionsParameter();
    }
    @Override
    public void run()
    {
        M_param.execute();
    }

    @Parameter private OptionsParameter M_param;
}

/* This parameter holds all options.  The first few things aren't in
 * OptionsCommand directly because I want messages too.  I should add messages
 * to dynamic_parameters.
 */
class OptionsParameter extends HoldingParameter<Void> {
    public OptionsParameter()
    {
        super("Reconstruction Options");
    }
    @Override
    public void initialize()
    {
        M_wavelength = create_unit_param("Wavelength", P_units.wavelength());
        M_image = create_unit_param("Image Dimensions", P_units.image());
        M_z = create_unit_param("Z", P_units.z());
        // Not using add_parameter because there's an unchecked conversion
        M_plugins = new PluginOptionsParameter<MainReconstructionPlugin>(
                                  MainReconstructionPlugin.class);
        add_premade_parameter(M_plugins);
    }
    @Override
    public void add_to_dialog(DPDialog dialog)
    {
        dialog.add_message("Unit Options");
        M_wavelength.add_to_dialog(dialog);
        M_image.add_to_dialog(dialog);
        M_z.add_to_dialog(dialog);
        dialog.add_message("Plugin Options");
        M_plugins.add_to_dialog(dialog);
    }
    @Override public Void get_value() {return null;}
    public void execute()
    {
        P_units.set_wavelength(M_wavelength.get_value());
        P_units.set_image(M_image.get_value());
        P_units.set_z(M_z.get_value());
        M_plugins.execute();
    }

    private ChoiceParameter create_unit_param(String name,
                                              DistanceUnits default_unit)
    {
        String default_string = null;
        switch (default_unit) {
            case Nano: default_string = "Nanometers"; break;
            case Micro: default_string = "Micrometers"; break;
            case Milli: default_string = "Millimeters"; break;
            case Centi: default_string = "Centimeters"; break;
            case Meter: default_string = "Meters"; break;
        }
        return add_parameter(ChoiceParameter.class,name, units, default_string);
    }

    private ChoiceParameter M_wavelength;
    private ChoiceParameter M_image;
    private ChoiceParameter M_z;
    private PluginOptionsParameter<MainReconstructionPlugin> M_plugins;
    private static String[] units = {"Nanometers", "Micrometers", "Millimeters",
                                     "Centimeters", "Meters"};

    @Parameter private UnitService P_units;
    @Parameter private ReconstructionPluginService P_plugins;
}

/* This holds a bunch of plugins of a given type, which you can only select one
 * at a time through a ChoiceParameter.
 */
class PluginOptionsParameter<T extends ReconstructionPlugin>
      extends HoldingParameter<Void>
{
    // This is for when you have a class but don't have the type
    public static <U extends ReconstructionPlugin>
           PluginOptionsParameter<U> create(Class<U> cls)
    {
        return new PluginOptionsParameter<U>(cls);
    }
    public PluginOptionsParameter(Class<T> cls)
    {
        super(cls.getName());
        M_class = cls;
    }
    @Override
    public void initialize()
    {
        M_plugins = P_plugins.get_all_plugins(M_class);
        M_parameters = new HashMap<>();
        ArrayList<String> choices = new ArrayList<>();
        for (T plugin : M_plugins) {
            choices.add(plugin.get_name());
        }
        String[] choices_ar = new String[choices.size()];
        choices_ar = choices.toArray(choices_ar);
        M_choice = add_parameter(ChoiceParameter.class, "Plugin", choices_ar);
        for (int i = 0; i < M_plugins.size(); ++i) {
            String name = choices.get(i);
            T plugin = M_plugins.get(i);
            // We create this here instead of in add_parameter to stop an
            // unchecked conversion
            SinglePluginOptionsParameter<T> param
                = new SinglePluginOptionsParameter<>(plugin);
            M_parameters.put(name, param);
            S_all_parameters.add(param);
            add_premade_parameter(param);
        }
        set_visibilities();
    }
    @Override
    public void read_from_dialog()
    {
        super.read_from_dialog();
        set_visibilities();
    }
    @Override
    public void read_from_prefs(Class<?> cls, String name)
    {
        super.read_from_prefs(cls, name);
        set_visibilities();
    }
    // We want all errors, even for invisible parameters.
    @Override
    public String get_error()
    {
        String result = super.get_error();
        if (result == null) {
            for (SinglePluginOptionsParameter<T> param : M_parameters.values()){
                param.check_for_errors();
                result = param.get_error();
                if (result != null) break;
            }
        }
        return result;
    }
    @Override public Void get_value() {return null;}
    public void execute()
    {
        for (SinglePluginOptionsParameter<T> param : M_parameters.values()) {
            param.execute();
        }
    }

    private void set_visibilities()
    {
        for (SinglePluginOptionsParameter<T> param : M_parameters.values()) {
            param.set_new_visibility(false);
        }
        M_parameters.get(M_choice.get_value()).set_new_visibility(true);
    }

    @Parameter private ReconstructionPluginService P_plugins;

    private Class<T> M_class;
    private List<T> M_plugins;
    private ChoiceParameter M_choice;
    private HashMap<String, SinglePluginOptionsParameter<T>> M_parameters;
    static ArrayList<SinglePluginOptionsParameter<?>> S_all_parameters
        = new ArrayList<>();
}

/* This holds the options for a single plugin.  It always at least has enabling
 * and disabling.
 */
class SinglePluginOptionsParameter<T extends ReconstructionPlugin>
      extends HoldingParameter<T>
{
    public SinglePluginOptionsParameter(T plugin)
    {
        super(plugin.getClass().getName());
        M_plugin = plugin;
    }
    @Override
    public void initialize()
    {
        boolean enabled = P_plugins.is_enabled(M_plugin.getClass());
        M_enabled = add_parameter(BoolParameter.class, "Enabled", enabled);
        DParameter<?> param = M_plugin.options_param();
        if (param != null) {
            add_premade_parameter(param);
        }
        List<Class<? extends ReconstructionPlugin>> sub_classes
            = M_plugin.sub_plugins();
        if (sub_classes != null) {
            for (Class<? extends ReconstructionPlugin> cls : sub_classes) {
                PluginOptionsParameter<? extends ReconstructionPlugin> sub
                    = PluginOptionsParameter.create(cls);
                M_subs.add(sub);
                add_premade_parameter(sub);
            }
        }
        check_for_errors();
    }
    @Override public void read_from_dialog()
    {
        super.read_from_dialog();
        check_for_errors();
    }
    // Disable reading from prefs, because a programmer could have changed the
    // options somewhere else.
    @Override public void read_from_prefs(Class<?> cls, String name) {}
    @Override public T get_value() {return M_plugin;}
    public void execute()
    {
        if (M_enabled.get_value()) P_plugins.enable(M_plugin.getClass());
        else P_plugins.disable(M_plugin.getClass());
        for (PluginOptionsParameter<?> sub : M_subs) {
            sub.execute();
        }
        M_plugin.read_options();
    }
    public void check_for_errors()
    {
        String error = null;
        if (!M_enabled.get_value()) {
            for (SinglePluginOptionsParameter<?> param
                    : PluginOptionsParameter.S_all_parameters) {
                if (param.M_enabled.get_value()) {
                    List<Class<? extends ReconstructionPlugin>> dependencies
                        = param.M_plugin.dependencies();
                    if (dependencies != null) {
                        for (Class<? extends ReconstructionPlugin> cls
                                : dependencies) {
                            if (cls == M_plugin.getClass()) {
                                error = "The plugin " + param.M_plugin
                                    .get_name() + " is enabled but depends on "
                                    + M_plugin.get_name() + ", which is "
                                    + "disabled.";
                            }
                        }
                    }
                }
            }
        }
        set_error(error);
    }

    @Parameter private ReconstructionPluginService P_plugins;
    private BoolParameter M_enabled;
    private ArrayList<PluginOptionsParameter<? extends ReconstructionPlugin>>
        M_subs = new ArrayList<>();
    private T M_plugin;
}
