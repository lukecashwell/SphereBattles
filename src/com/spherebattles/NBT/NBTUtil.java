package com.spherebattles.NBT;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL.TypeReference;

public final class NBTUtil {

   public static NBTTagCompound update(DataFixer p_210822_0_, TypeReference p_210822_1_, NBTTagCompound p_210822_2_, int p_210822_3_) {
      return update(p_210822_0_, p_210822_1_, p_210822_2_, p_210822_3_, 1631);
   }

   public static NBTTagCompound update(DataFixer dataFixer, TypeReference type, NBTTagCompound p_210821_2_, int version, int newVersion) {
      return (NBTTagCompound)dataFixer.update(type, new Dynamic<>(NBTDynamicOps.INSTANCE, p_210821_2_), version, newVersion).getValue();
   }
}