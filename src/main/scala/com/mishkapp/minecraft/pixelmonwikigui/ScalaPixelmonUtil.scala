package com.mishkapp.minecraft.pixelmonwikigui

import com.happyzleaf.pixelmonwikigui.helper.{FuckScala, PixelmonHelper, ReflectionHelper}
import com.pixelmonmod.pixelmon.api.spawning.SpawnSet
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnInfoPokemon
import com.pixelmonmod.pixelmon.api.spawning.util.SetLoader
import com.pixelmonmod.pixelmon.entities.SpawnLocationType
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats
import com.pixelmonmod.pixelmon.enums.EnumType
import net.minecraft.block.Block
import net.minecraft.util.text.TextFormatting

import java.util
import java.util.Collections
import scala.collection.JavaConverters._

class ScalaPixelmonUtil {
  def getSpawnBiomes(pokemon: BaseStats): util.List[String] = {
    SetLoader.getAllSets[SpawnSet].stream.iterator.asScala
      .flatMap(s => s.spawnInfos.asScala
        .filter(_.isInstanceOf[SpawnInfoPokemon])
        .map(_.asInstanceOf[SpawnInfoPokemon])
        .filter(_.getPokemonSpec.name.equalsIgnoreCase(pokemon.getPokemonName))
        .filter(si => si.spawnSpecificBossRate == null)
        .map(si => {
          si.condition.biomes.asScala
            .map(biome => {
              val biomeName = ReflectionHelper.getBiomeName(biome).orElse(null);
              if (biomeName == null) {
                TextFormatting.ITALIC + TextFormatting.RED.toString + "JAVA ERROR FOR " + biome.getClass.getSimpleName
              } else {
                TextFormatting.GOLD + biomeName + " " + FuckScala.formatDecimal(si.rarity / 255d * 100d) + "%"
              }

              // Removed in 1.1.3 due to client request
              /*else if (si.spawnSpecificBossRate != null) {
                TextFormatting.BOLD + TextFormatting.RED.toString + "Boss: " + TextFormatting.RESET + TextFormatting.GOLD + biomeName + " " + (si.rarity / 255.0D * 100.0D).asInstanceOf[Int].toString + "%"
              } else {
                TextFormatting.GOLD + biomeName + " " + (si.rarity / 255.0D * 100.0D).asInstanceOf[Int].toString + "%"
              }*/
            })
        }
        ))
      .flatten
      .toList
      .asJava
  }

  def getSpawnTimes(pokemon: BaseStats): util.List[String] = {
    val result = SetLoader.getAllSets[SpawnSet].stream.iterator.asScala
      .flatMap(s => s.spawnInfos.asScala
        .filter(_.isInstanceOf[SpawnInfoPokemon])
        .map(_.asInstanceOf[SpawnInfoPokemon])
        .filter(_.getPokemonSpec.name.equalsIgnoreCase(pokemon.getPokemonName))
        .filter(_.condition.times != null)
        .map(si => {
          si.condition.times.asScala
            .distinct
            .filter(t => t != null)
            .map(t => PixelmonHelper.formatTime(t))
        }
        ))
      .flatten
      .toList
      .distinct
      .asJava

    if (result.isEmpty) {
      List[String](TextFormatting.GOLD.toString + "Any time of the day").asJava
    } else {
      result
    }
  }

  def getCatchRate(pokemon: BaseStats): util.List[String] = {
    var res = List[String](TextFormatting.GRAY + "Base rate: " + (pokemon.getCatchRate.toDouble / 255.0D * 100.0D).toInt.toString + "%")
    val males = pokemon.getMalePercent
    if (males == -1) {
      res :+= TextFormatting.GRAY + "⚲ Genderless"
    } else {
      res :+= TextFormatting.BLUE + "♂ Male: " + males.toString + "%"
      res :+= TextFormatting.RED + "♀ Female: " + Math.abs(100 - males).toString + "%"
    }
    res.asJava

  }

  def getAbilities(pokemon: BaseStats): util.List[String] = {
    pokemon.getAbilitiesArray
      .filter(_ != null)
      .map(a => AbilityBase.getAbility(a).orElse(null))
      .filter(_ != null)
      .map(a => TextFormatting.GOLD + a.getLocalizedName)
      .toList
      .asJava
  }

  def getEvolutions(pokemon: BaseStats): util.List[String] = {
    pokemon.getEvolutions.asScala.map(e => TextFormatting.GOLD + e.to.name)
      .toList
      .asJava
  }

  def getTypeEffectiveness(pokemon: BaseStats): util.List[String] = {
    val types = pokemon.getTypeList
    EnumType.values()
      .filter(t => EnumType.getTotalEffectiveness(types, t) != 1.0f)
      .map(t => PixelmonHelper.getTypeColor(t) + t.getLocalizedName + " " + prepareEffectiveness(EnumType.getTotalEffectiveness(types, t)) + "x")
      .toList
      .asJava
  }

  def prepareEffectiveness(eff: Float): String = eff match {
    case e if ~=(e, 0f, 0.05f) => "0"
    case e if ~=(e, 0.25f, 0.05f) => "1/4"
    case e if ~=(e, 0.5f, 0.05f) => "1/2"
    case e if ~=(e, 2f, 0.05f) => "2"
    case e if ~=(e, 4f, 0.05f) => "4"
  }

  def getBreeding(pokemon: BaseStats, typeBlocks: util.EnumMap[EnumType, util.HashMap[Block, Integer]]): util.List[String] = {
    typeBlocks.asScala
      .keySet
      .filter(e => e == pokemon.getType1 || e == pokemon.getType2)
      .map(t => typeBlocks
        .get(t)
        .asScala
        .toList
        .sortWith((e1, e2) => e1._2 > e2._2)
        .foldLeft(List[String](TextFormatting.BOLD.toString + PixelmonHelper.getTypeColor(t) + t.getName)) { (z, e) => z :+ PixelmonHelper.getTypeColor(t) + e._1.getLocalizedName + " " + e._2.toString })
      .foldLeft(List[String]()) { (z, s) => z ++ s }
      .asJava
  }

  def getEVYield(pokemon: BaseStats): util.List[String] = {
    pokemon.evYields.asScala
      .map { case (k, v) => PixelmonHelper.getStatColor(k) + k.getLocalizedName + ": " + v.toString }
      .toList
      .asJava
  }

  def getDrops(pokemon: BaseStats): util.List[String] = {
    //    val dropInfo = Util.getDropInfo(pokemon.getBaseStats.pokemon)
    //    if (dropInfo == null) {
    //      return Seq[Text](Text.of(TextFormatting.RED, "???"))
    //    }
    //
    //    var res = Seq[Text]()
    //
    //    if (dropInfo.mainDrop != null) {
    //      res :+= Text.of(TextFormatting.GRAY, dropInfo.mainDrop.getTranslation.get(), " ", TextFormatting.WHITE, dropInfo.mainDropMin.toString, "-", dropInfo.mainDropMax.toString)
    //    }
    //    if (dropInfo.opt1Drop != null) {
    //      res :+= Text.of(TextFormatting.AQUA, dropInfo.opt1Drop.getTranslation.get(), " ", TextFormatting.WHITE, dropInfo.opt1DropMin.toString, "-", dropInfo.opt1DropMax.toString)
    //    }
    //    if (dropInfo.opt2Drop != null) {
    //      res :+= Text.of(TextFormatting.AQUA, dropInfo.opt2Drop.getTranslation.get(), " ", TextFormatting.WHITE, dropInfo.opt2DropMin.toString, "-", dropInfo.opt2DropMax.toString)
    //    }
    //    if (dropInfo.rareDrop != null) {
    //      res :+= Text.of(TextFormatting.GOLD, dropInfo.rareDrop.getTranslation.get(), " ", TextFormatting.WHITE, dropInfo.rareDropMin.toString, "-", dropInfo.rareDropMax.toString)
    //    }
    //    res
    return Collections.emptyList()
  }

  def getMoves(pokemon: BaseStats): util.List[String] = {
    pokemon.getLevelupMoves.asScala
      .map { case (l, m) => TextFormatting.GOLD + l.toString + ": " + TextFormatting.AQUA + m.asScala.map(_.getAttackName).mkString(", ") }
      .toList
      .asJava
  }

  def getTMMoves(pokemon: BaseStats): util.List[String] = {
    val tms = pokemon.getTMMoves.asScala.map(a => a.getAttackName).toList
    val hms = pokemon.getHmMoves.asScala.map(a => a.getAttackName).toList

    List[String](
      TextFormatting.GOLD + (tms ++ hms).mkString(", ")
    )
      .asJava
  }

  def getTutorMoves(pokemon: BaseStats): util.List[String] = {
    List[String](
      TextFormatting.GOLD + pokemon.getTutorMoves.asScala.map(a => a.getActualMove.getLocalizedName).mkString(", ")
    )
      .asJava
  }

  def ~=(x: Float, y: Float, precision: Float): Boolean = {
    if ((x - y).abs < precision) true else false
  }

  def getSpawnLocation(pokemon: BaseStats): util.List[String] = {
    pokemon.spawnLocations.map {
      case SpawnLocationType.Air => (TextFormatting.WHITE + "Air")
      case SpawnLocationType.AirPersistent => (TextFormatting.WHITE + "Air (Persistent)")
      case SpawnLocationType.Boss => (TextFormatting.RED + "Boss")
      case SpawnLocationType.Land => (TextFormatting.GREEN + "Land")
      case SpawnLocationType.LandNPC => (TextFormatting.GREEN + "Land (NPC)")
      case SpawnLocationType.LandVillager => (TextFormatting.GREEN + "Land (Villager)")
      case SpawnLocationType.Legendary => (TextFormatting.GOLD + "Legendary")
      case SpawnLocationType.UnderGround => (TextFormatting.GRAY + "Underground")
      case SpawnLocationType.Water => (TextFormatting.BLUE + "Water")
    }
      .toList
      .asJava
  }
}
