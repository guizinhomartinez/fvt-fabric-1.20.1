package me.flourick.fvt.settings;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Allows accessing FVT settings menu directly from ModMenu mod.
 * 
 * @author Flourick
 */
public class ModMenuSettingsImpl implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return FVTSettingsScreen::getNewScreen;
	}
}
