package juniper.monotone.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplate.PalettedBlockInfoList;

@Mixin(StructureTemplate.class)
public interface StructureTemplateAccessor {
    @Accessor
    public List<PalettedBlockInfoList> getBlockInfoLists();
}
