package com.spherebattles.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.spherebattles.NBT.*;
import com.spherebattles.main.Main;

public class DataTools {
	
	public static Location nbtToLocation(NBTTagList location) {
		String world = location.getString(0);
		float x      = location.getFloat(1);
		float y      = location.getFloat(2);
		float z      = location.getFloat(3);
		float pitch  = location.getFloat(4);
		float yaw    = location.getFloat(5);
		return new Location(Bukkit.getWorld(world), x, y, z, pitch, yaw);
	}	
	
	public static NBTTagList locationToNbt(Location location) {
		NBTTagList nbt = new NBTTagList();
		nbt.add(location.getWorld().getName());
		nbt.add((float)location.getX());
		nbt.add((float)location.getY());
		nbt.add((float)location.getZ());
		nbt.add((float)location.getPitch());
		nbt.add((float)location.getYaw());
		return nbt;
	}
	
	
   /**
    * Load the gzipped compound from the inputstream.
    */
   public static NBTTagCompound readCompressed(InputStream is) throws IOException {
      DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(is)));

      NBTTagCompound nbttagcompound;
      try {
         nbttagcompound = read(datainputstream, NBTSizeTracker.INFINITE);
      } finally {
         datainputstream.close();
      }

      return nbttagcompound;
   }

   /**
    * Write the compound, gzipped, to the outputstream.
    */
   public static void writeCompressed(NBTTagCompound compound, OutputStream outputStream) throws IOException {
      DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));

      try {
         write(compound, dataoutputstream);
      } finally {
         dataoutputstream.close();
      }

   }

   public static void safeWrite(NBTTagCompound compound, File fileIn) throws IOException {
      File file1 = new File(fileIn.getAbsolutePath() + "_tmp");
      if (file1.exists()) {
         file1.delete();
      }

      write(compound, file1);
      if (fileIn.exists()) {
         fileIn.delete();
      }

      if (fileIn.exists()) {
         throw new IOException("Failed to delete " + fileIn);
      } else {
         file1.renameTo(fileIn);
      }
   }

   public static void write(NBTTagCompound compound, File fileIn) throws IOException {
      DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(fileIn));

      try {
    	  writeCompressed(compound, dataoutputstream);
      } finally {
         dataoutputstream.close();
      }

   }

   public static NBTTagCompound readNBTFile(String file) {
	   NBTTagCompound nbt = null;
	   try{
		   nbt = read(new File(Main.getInstance().dataFile, file));			   
	   } catch (Exception e) { e.printStackTrace(); System.err.println("[SB] -> Unable to read file: '" + file + "'");}
	   if(nbt == null) {
		   nbt = new NBTTagCompound();
	   }
	   return nbt;
			   
   }
   
   public static void writeNBTFile(String fileName, NBTTagCompound tag) {
	   File file = new File(Main.getInstance().dataFile, fileName);
	   if(!file.exists()) {
		   file.getParentFile().mkdirs();
		   try {
			file.createNewFile();
		   } catch (IOException e) {
			   e.printStackTrace();
		   }
	   }
	   try{
		   write(tag, file);			   
	   } catch (Exception e) { e.printStackTrace(); }
   }
   
   
   @Nullable
   public static NBTTagCompound read(File fileIn) throws IOException {
      if (!fileIn.exists()) {
         return null;
      } else {
         DataInputStream datainputstream = new DataInputStream(new FileInputStream(fileIn));

         NBTTagCompound nbttagcompound;
         try {
            nbttagcompound = readCompressed(datainputstream);
         } finally {
            datainputstream.close();
         }

         return nbttagcompound;
      }
   }

   /**
    * Reads from a CompressedStream.
    */
   public static NBTTagCompound read(DataInputStream inputStream) throws IOException {
      return read(inputStream, NBTSizeTracker.INFINITE);
   }

   /**
    * Reads the given DataInput, constructs, and returns an NBTTagCompound with the data from the DataInput
    */
   public static NBTTagCompound read(DataInput input, NBTSizeTracker accounter) throws IOException {
      INBTBase inbtbase = read(input, 0, accounter);
      if (inbtbase instanceof NBTTagCompound) {
         return (NBTTagCompound)inbtbase;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(NBTTagCompound compound, DataOutput output) throws IOException {
      writeTag(compound, output);
   }

   private static void writeTag(INBTBase tag, DataOutput output) throws IOException {
      output.writeByte(tag.getId());
      if (tag.getId() != 0) {
         output.writeUTF("");
         tag.write(output);
      }
   }

   private static INBTBase read(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
      byte b0 = input.readByte();
      accounter.read(8); // Forge: Count everything!
      if (b0 == 0) {
         return new NBTTagEnd();
         
      } else {
         NBTSizeTracker.readUTF(accounter, input.readUTF()); //Forge: Count this string.
         accounter.read(32); //Forge: 4 extra bytes for the object allocation.
         INBTBase inbtbase = INBTBase.create(b0);
         try {
            inbtbase.read(input, depth, accounter);
            return inbtbase;
         } catch (IOException e) { e.printStackTrace(); }
      }
      return null;
   }
}