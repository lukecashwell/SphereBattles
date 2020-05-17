package com.spherebattles.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class NBTTagStringArray extends NBTTagCollection<NBTTagString> {
   private String[] data;

   NBTTagStringArray() {
   }

   public NBTTagStringArray(String[] array) {
      this.data = array;
   }

   public NBTTagStringArray(List<String> list) {
      this(toArray(list));
   }

   private static String[] toArray(List<String> list) {
      String[] aString = new String[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         String string = list.get(i);
         aString[i] = string == null ? "" : string;
      }

      return aString;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.data.length);

      for(String i : this.data) {
         output.writeUTF(i);
      }

   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
	  sizeTracker.read(32L);
	  int i = input.readInt();
     
      this.data = new String[i];

      for(int j = 0; j < i; ++j) {
    	 String utf = input.readUTF();
    	 NBTSizeTracker.readUTF(sizeTracker, utf);
         this.data[j] = utf;
      }
      
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 13;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[S;");

      for(int i = 0; i < this.data.length; i++) {
         if (i != 0) {
            stringbuilder.append(',');
         }
         stringbuilder.append('\"');
         for(int j = 0; j < this.data[i].length(); j++) {
             char c0 = this.data[i].charAt(j);
             if (c0 == '\\' || c0 == '"') {
                stringbuilder.append('\\');
             }

             stringbuilder.append(c0);
         }
         stringbuilder.append('\"');
      }

      return stringbuilder.append(']').toString();
   }

   /**
    * Creates a clone of the tag.
    */
   public NBTTagStringArray copy() {
      String[] aString = new String[this.data.length];
      System.arraycopy(this.data, 0, aString, 0, this.data.length);
      return new NBTTagStringArray(aString);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagStringArray && Arrays.equals(this.data, ((NBTTagStringArray)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public String[] getAsStringArray() {
      return this.data;
   }

   public List<String> getAsStringList() {
	   List<String> list = Arrays.asList(this.data);   
	   return list;
   }
   
   public int size() {
      return this.data.length;
   }

   public NBTTagString getTag(int p_197647_1_) {
      return new NBTTagString(this.data[p_197647_1_]);
   }

   public void setTag(int p_197648_1_, INBTBase p_197648_2_) {
      this.data[p_197648_1_] = ((NBTPrimitive)p_197648_2_).getString();
   }

   public void removeTag(int p_197649_1_) {
	      this.data = (String[]) remove(this.data, p_197649_1_);
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