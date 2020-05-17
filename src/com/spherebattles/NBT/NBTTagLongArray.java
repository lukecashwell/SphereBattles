package com.spherebattles.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class NBTTagLongArray extends NBTTagCollection<NBTTagLong> {
   private long[] data;

   NBTTagLongArray() {
   }

   public NBTTagLongArray(long[] p_i47524_1_) {
      this.data = p_i47524_1_;
   }

   public NBTTagLongArray(List<Long> p_i47525_1_) {
      this(toArray(p_i47525_1_));
   }

   private static long[] toArray(List<Long> p_193586_0_) {
      long[] along = new long[p_193586_0_.size()];

      for(int i = 0; i < p_193586_0_.size(); ++i) {
         Long olong = p_193586_0_.get(i);
         along[i] = olong == null ? 0L : olong;
      }

      return along;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.data.length);

      for(long i : this.data) {
         output.writeLong(i);
      }

   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(192L);
      int i = input.readInt();
      sizeTracker.read((long)(64 * i));
      this.data = new long[i];

      for(int j = 0; j < i; ++j) {
         this.data[j] = input.readLong();
      }

   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 12;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[L;");

      for(int i = 0; i < this.data.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.data[i]).append('L');
      }

      return stringbuilder.append(']').toString();
   }

   /**
    * Creates a clone of the tag.
    */
   public NBTTagLongArray copy() {
      long[] along = new long[this.data.length];
      System.arraycopy(this.data, 0, along, 0, this.data.length);
      return new NBTTagLongArray(along);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagLongArray && Arrays.equals(this.data, ((NBTTagLongArray)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public long[] getAsLongArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public NBTTagLong getTag(int p_197647_1_) {
      return new NBTTagLong(this.data[p_197647_1_]);
   }

   public void setTag(int p_197648_1_, INBTBase p_197648_2_) {
      this.data[p_197648_1_] = ((NBTPrimitive)p_197648_2_).getLong();
   }

   public void removeTag(int p_197649_1_) {
	      this.data = (long[]) remove(this.data, p_197649_1_);
   }
	   
   private static Object remove(final Object array, final int index) {
       final int length = Array.getLength(array);
       if (index < 0 || index >= length) {
           throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
       }

       final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
       System.arraycopy(array, 0, result, 0, index);
       if (index < length - 1) {
           System.arraycopy(array, index + 1, result, index, length - index - 1);
       }

       return result;
   }
	   
}