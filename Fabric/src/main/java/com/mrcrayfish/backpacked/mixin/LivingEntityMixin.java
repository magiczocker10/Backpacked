package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.enchantment.LootedEnchantment;
import com.mrcrayfish.backpacked.entity.ILootCapture;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "dropAllDeathLoot", at = @At(value = "HEAD"))
    private void backpackedOnDropLootPre(DamageSource damageSource, CallbackInfo ci)
    {
        ((ILootCapture) this).backpackedStartCapturingDrop();
    }

    @Inject(method = "dropAllDeathLoot", at = @At(value = "TAIL"))
    private void backpackedOnDropLootPost(DamageSource damageSource, CallbackInfo ci)
    {
        LivingEntity entity = (LivingEntity) (Object) this;
        List<ItemEntity> drops = ((ILootCapture) this).backpackedGetCapturedDrops();
        if(drops != null)
        {
            //TODO move to an event
            if(!LootedEnchantment.onDropLoot(drops, damageSource))
            {
                drops.forEach(e -> entity.level.addFreshEntity(e));
            }
            ((ILootCapture) this).backpackedEndCapturingDrop();
        }
    }
}
