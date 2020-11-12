package vazkii.ambience.Util;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.Ambience;
import vazkii.ambience.Screens.EditAreaContainer;
import vazkii.ambience.Screens.GuiContainerMod;
import vazkii.ambience.Screens.SpeakerContainer;

public class ModContainerTypes {
	
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Ambience.MODID);
	
	public static final RegistryObject<ContainerType<GuiContainerMod>> CREATEAREA_CONTAINER = CONTAINER_TYPES.register("guicontainer", () -> IForgeContainerType.create(GuiContainerMod::new));
	
	public static final RegistryObject<ContainerType<EditAreaContainer>> EDITAREA_CONTAINER = CONTAINER_TYPES.register("editarea_container", () -> IForgeContainerType.create(EditAreaContainer::new));
	
	public static final RegistryObject<ContainerType<SpeakerContainer>> SPEAKER_CONTAINER = CONTAINER_TYPES.register("speaker_container", () -> IForgeContainerType.create(SpeakerContainer::new));
		
}
