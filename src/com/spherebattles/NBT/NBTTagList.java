package com.spherebattles.NBT;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class NBTTagList extends NBTTagCollection<INBTBase> {
   /** The array list containing the tags encapsulated in this list. */
   private List<INBTBase> tagList = Lists.newArrayList();

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
	  List<Byte> tagTypes = Lists.newArrayList();
	  if (!this.tagList.isEmpty()) {
    	 for(int i = 0; i < this.tagList.size(); ++i) {
    		 tagTypes.add(this.tagList.get(i).getId());
    	 }
      }
      
      output.writeInt(this.tagList.size());
  
      for(int i = 0; i < this.tagList.size(); ++i) {
    	 output.writeByte(tagTypes.get(i));
    	 this.tagList.get(i).write(output);
      }

   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(296L);
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         int i = input.readInt();
         sizeTracker.read(32L * (long)i);
         this.tagList = Lists.newArrayListWithCapacity(i);

         for(int j = 0; j < i; ++j) {
            INBTBase type = INBTBase.create((byte)1);
            type.read(input, depth + 1, sizeTracker);
            INBTBase base = INBTBase.create(((NBTTagByte)type).getByte());
            base.read(input, depth + 1, sizeTracker);
            this.tagList.add(base);
         }

      }
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 9;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[");

      for(int i = 0; i < this.tagList.size(); ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.tagList.get(i));
      }

      return stringbuilder.append(']').toString();
   }

   public boolean add(byte p_add_1_) {
	   return add(new NBTTagByte(p_add_1_));
   }
   
   public boolean add(short p_add_1_) {
	   return add(new NBTTagShort(p_add_1_));
   }
   
   public boolean add(float p_add_1_) {
	   return add(new NBTTagFloat(p_add_1_));
   }
   
   public boolean add(double p_add_1_) {
	   return add(new NBTTagDouble(p_add_1_));
   }
   
   public boolean add(int p_add_1_) {
	   return add(new NBTTagInt(p_add_1_));
   }
   
   public boolean add(long p_add_1_) {
	   return add(new NBTTagLong(p_add_1_));
   }
   
   public boolean add(String p_add_1_) {
	   return add(new NBTTagString(p_add_1_));
   }
   
   public boolean add(INBTBase p_add_1_) {
      if (p_add_1_.getId() == 0) {
    	  System.err.println("[warn] Invalid TagEnd added to ListTag");
         return false;
      } else {
         this.tagList.add(p_add_1_);
         return true;
      }
   }

   public INBTBase set(int p_set_1_, INBTBase p_set_2_) {
      if (p_set_2_.getId() == 0) {
    	  System.err.println("[warn] Invalid TagEnd added to ListTag");
         return this.tagList.get(p_set_1_);
      } else if (p_set_1_ >= 0 && p_set_1_ < this.tagList.size()) {
         return this.tagList.set(p_set_1_, p_set_2_);
      } else {
         System.err.println("[warn] index out of bounds to set tag in tag list");
         return null;
      }
   }

   public INBTBase remove(int p_remove_1_) {
      return this.tagList.remove(p_remove_1_);
   }

   public boolean isEmpty() {
      return this.tagList.isEmpty();
   }

   /**
    * Retrieves the NBTTagCompound at the specified index in the list
    */
   public NBTTagCompound getCompound(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(i);
         if (inbtbase.getId() == 10) {
            return (NBTTagCompound)inbtbase;
         }
      }

      return null;
   }

   public NBTTagList getList(int iIn) {
      if (iIn >= 0 && iIn < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(iIn);
         if (inbtbase.getId() == 9) {
            return (NBTTagList)inbtbase;
         }
      }

      return null;
   }

   public short getShort(int iIn) {
      if (iIn >= 0 && iIn < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(iIn);
         if (inbtbase.getId() == 2) {
            return ((NBTTagShort)inbtbase).getShort();
         }
      }

      return 0;
   }

   public int getInt(int iIn) {
      if (iIn >= 0 && iIn < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(iIn);
         if (inbtbase.getId() == 3) {
            return ((NBTTagInt)inbtbase).getInt();
         }
      }

      return 0;
   }

   public int[] getIntArray(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(i);
         if (inbtbase.getId() == 11) {
            return ((NBTTagIntArray)inbtbase).getIntArray();
         }
      }

      return new int[0];
   }

   public double getDouble(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(i);
         if (inbtbase.getId() == 6) {
            return ((NBTTagDouble)inbtbase).getDouble();
         }
      }

      return 0.0D;
   }

   public float getFloat(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(i);
         if (inbtbase.getId() == 5) {
            return ((NBTTagFloat)inbtbase).getFloat();
         }
      }

      return 0.0F;
   }

   /**
    * Retrieves the tag String value at the specified index in the list
    */
   public String getString(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(i);
         return inbtbase.getId() == 8 ? inbtbase.getString() : inbtbase.toString();
      } else {
         return "";
      }
   }

   public INBTBase get(int p_get_1_) {
      return (INBTBase)(p_get_1_ >= 0 && p_get_1_ < this.tagList.size() ? this.tagList.get(p_get_1_) : new NBTTagEnd());
   }

   public int size() {
      return this.tagList.size();
   }

   public INBTBase getTag(int p_197647_1_) {
      return this.tagList.get(p_197647_1_);
   }

   public void setTag(int p_197648_1_, INBTBase p_197648_2_) {
      this.tagList.set(p_197648_1_, p_197648_2_);
   }

   public void removeTag(int p_197649_1_) {
      this.tagList.remove(p_197649_1_);
   }

   /**
    * Creates a clone of the tag.
    */
   public NBTTagList copy() {
      NBTTagList nbttaglist = new NBTTagList();
      for(INBTBase inbtbase : this.tagList) {
         INBTBase inbtbase1 = inbtbase.copy();
         nbttaglist.tagList.add(inbtbase1);
      }

      return nbttaglist;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagList && Objects.equals(this.tagList, ((NBTTagList)p_equals_1_).tagList);
      }
   }

   public int hashCode() {
      return this.tagList.hashCode();
   }

}