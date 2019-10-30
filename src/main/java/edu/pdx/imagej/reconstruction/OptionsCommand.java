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
        PluginOptionsParameter.S_allParameters.clear();
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
 * to dynamicParameters.
 */
class OptionsParameter extends HoldingParameter<Void> {
    public OptionsParameter()
    {
        super("Reconstruction Options");
    }
    @Override
    public void initialize()
    {
        M_wavelength = createUnitParam("Wavelength", P_units.wavelength());
        M_image = createUnitParam("Image Dimensions", P_units.image());
        M_z = createUnitParam("Z", P_units.z());
        M_plugins = new PluginOptionsParameter<MainReconstructionPlugin>(
                                  MainReconstructionPlugin.class);
        addParameter(M_plugins);
    }
    @Override
    public void addToDialog(DPDialog dialog)
    {
        dialog.addMessage("Unit Options");
        M_wavelength.addToDialog(dialog);
        M_image.addToDialog(dialog);
        M_z.addToDialog(dialog);
        dialog.addMessage("Plugin Options");
        M_plugins.addToDialog(dialog);
    }
    @Override public Void getValue() {return null;}
    public void execute()
    {
        P_units.setWavelength(M_wavelength.getValue());
        P_units.setImage(M_image.getValue());
        P_units.setZ(M_z.getValue());
        M_plugins.execute();
    }

    private ChoiceParameter createUnitParam(String name,
                                              DistanceUnits defaultUnit)
    {
        String defaultString = null;
        switch (defaultUnit) {
            case Nano: defaultString = "Nanometers"; break;
            case Micro: defaultString = "Micrometers"; break;
            case Milli: defaultString = "Millimeters"; break;
            case Centi: defaultString = "Centimeters"; break;
            case Meter: defaultString = "Meters"; break;
        }
        return addParameter(new ChoiceParameter(name, units, defaultString));
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
        M_plugins = P_plugins.getAllPlugins(M_class);
        M_parameters = new HashMap<>();
        ArrayList<String> choices = new ArrayList<>();
        for (T plugin : M_plugins) {
            choices.add(plugin.getName());
        }
        String[] choicesAr = new String[choices.size()];
        choicesAr = choices.toArray(choicesAr);
        M_choice = addParameter(new ChoiceParameter("Plugin", choicesAr));
        for (int i = 0; i < M_plugins.size(); ++i) {
            String name = choices.get(i);
            T plugin = M_plugins.get(i);
            SinglePluginOptionsParameter<T> param
                = new SinglePluginOptionsParameter<>(plugin);
            M_parameters.put(name, param);
            S_allParameters.add(param);
            addParameter(param);
        }
        setVisibilities();
    }
    @Override
    public void readFromDialog()
    {
        super.readFromDialog();
        setVisibilities();
    }
    @Override
    public void readFromPrefs(Class<?> cls, String name)
    {
        super.readFromPrefs(cls, name);
        setVisibilities();
    }
    // We want all errors, even for invisible parameters.
    @Override
    public String getError()
    {
        String result = super.getError();
        if (result == null) {
            for (SinglePluginOptionsParameter<T> param : M_parameters.values()){
                param.checkForErrors();
                result = param.getError();
                if (result != null) break;
            }
        }
        return result;
    }
    @Override public Void getValue() {return null;}
    public void execute()
    {
        for (SinglePluginOptionsParameter<T> param : M_parameters.values()) {
            param.execute();
        }
    }

    private void setVisibilities()
    {
        for (SinglePluginOptionsParameter<T> param : M_parameters.values()) {
            param.setNewVisibility(false);
        }
        M_parameters.get(M_choice.getValue()).setNewVisibility(true);
    }

    @Parameter private ReconstructionPluginService P_plugins;

    private Class<T> M_class;
    private List<T> M_plugins;
    private ChoiceParameter M_choice;
    private HashMap<String, SinglePluginOptionsParameter<T>> M_parameters;
    static ArrayList<SinglePluginOptionsParameter<?>> S_allParameters
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
        boolean enabled = P_plugins.isEnabled(M_plugin.getClass());
        M_enabled = addParameter(new BoolParameter("Enabled", enabled));
        DParameter<?> param = M_plugin.optionsParam();
        if (param != null) {
            addParameter(param);
        }
        List<Class<? extends ReconstructionPlugin>> subClasses
            = M_plugin.subPlugins();
        if (subClasses != null) {
            for (Class<? extends ReconstructionPlugin> cls : subClasses) {
                PluginOptionsParameter<? extends ReconstructionPlugin> sub
                    = PluginOptionsParameter.create(cls);
                M_subs.add(sub);
                addParameter(sub);
            }
        }
        checkForErrors();
    }
    @Override public void readFromDialog()
    {
        super.readFromDialog();
        checkForErrors();
    }
    // Disable reading from prefs, because a programmer could have changed the
    // options somewhere else.
    @Override public void readFromPrefs(Class<?> cls, String name) {}
    @Override public T getValue() {return M_plugin;}
    public void execute()
    {
        if (M_enabled.getValue()) P_plugins.enable(M_plugin.getClass());
        else P_plugins.disable(M_plugin.getClass());
        for (PluginOptionsParameter<?> sub : M_subs) {
            sub.execute();
        }
        M_plugin.readOptions();
    }
    public void checkForErrors()
    {
        String error = null;
        if (!M_enabled.getValue()) {
            for (SinglePluginOptionsParameter<?> param
                    : PluginOptionsParameter.S_allParameters) {
                if (param.M_enabled.getValue()) {
                    List<Class<? extends ReconstructionPlugin>> dependencies
                        = param.M_plugin.dependencies();
                    if (dependencies != null) {
                        for (Class<? extends ReconstructionPlugin> cls
                                : dependencies) {
                            if (cls == M_plugin.getClass()) {
                                error = "The plugin " + param.M_plugin
                                    .getName() + " is enabled but depends on "
                                    + M_plugin.getName() + ", which is "
                                    + "disabled.";
                            }
                        }
                    }
                }
            }
        }
        setError(error);
    }

    @Parameter private ReconstructionPluginService P_plugins;
    private BoolParameter M_enabled;
    private ArrayList<PluginOptionsParameter<? extends ReconstructionPlugin>>
        M_subs = new ArrayList<>();
    private T M_plugin;
}
