package vazkii.ambience.Util;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.Ambience;
import vazkii.ambience.Screens.GuiContainerMod;

public class ModContainerTypes {

	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Ambience.MODID);
	
	public static final RegistryObject<ContainerType<GuiContainerMod>> GUI_CONTAINER = CONTAINER_TYPES.register("guicontainer", () -> IForgeContainerType.create(GuiContainerMod::new));
		
}
