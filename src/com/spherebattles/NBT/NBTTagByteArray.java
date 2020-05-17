package com.spherebattles.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class NBTTagByteArray extends NBTTagCollection<NBTTagByte> {
   /** The byte array stored in the tag. */
   private byte[] data;

   NBTTagByteArray() {
   }

   public NBTTagByteArray(byte[] data) {
      this.data = data;
   }

   public NBTTagByteArray(List<Byte> p_i47529_1_) {
      this(toArray(p_i47529_1_));
   }

   private static byte[] toArray(List<Byte> p_193589_0_) {
      byte[] abyte = new byte[p_193589_0_.size()];

      for(int i = 0; i < p_193589_0_.size(); ++i) {
         Byte obyte = p_193589_0_.get(i);
         abyte[i] = obyte == null ? 0 : obyte;
      }

      return abyte;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.data.length);
      output.write(this.data);
   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(192L);
      int i = input.readInt();
      sizeTracker.read((long)(8 * i));
      this.data = new byte[i];
      input.readFully(this.data);
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 7;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[B;");

      for(int i = 0; i < this.data.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append((int)this.data[i]).append('B');
      }

      return stringbuilder.append(']').toString();
   }

   /**
    * Creates a clone of the tag.
    */
   public INBTBase copy() {
      byte[] abyte = new byte[this.data.length];
      System.arraycopy(this.data, 0, abyte, 0, this.data.length);
      return new NBTTagByteArray(abyte);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagByteArray && Arrays.equals(this.data, ((NBTTagByteArray)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public byte[] getByteArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public NBTTagByte getTag(int p_197647_1_) {
      return new NBTTagByte(this.data[p_197647_1_]);
   }

   public void setTag(int p_197648_1_, INBTBase p_197648_2_) {
      this.data[p_197648_1_] = ((NBTPrimitive)p_197648_2_).getByte();
   }

   public void removeTag(int p_197649_1_) {
      this.data = (byte[]) remove(this.data, p_197649_1_);
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