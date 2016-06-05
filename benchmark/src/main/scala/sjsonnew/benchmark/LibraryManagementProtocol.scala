package sjsonnew
package benchmark

import sbt.librarymanagement._
import java.net.URL

trait LibraryManagementProtocol extends BasicJsonProtocol {
  implicit val configurationStringIso = IsoString.iso[Configuration](_.name, Configurations.config)
  implicit val crossVersionStringIso = IsoString.iso[CrossVersion](
    _ match {
      case CrossVersion.Disabled => "disabled"
      case x => x.toString
    },
    _ match {
      case "disabled" => CrossVersion.Disabled
      case "full"     => CrossVersion.full
      case "binary"   => CrossVersion.binary
      case ""         => CrossVersion.Disabled
    })

  // final case class Artifact(name: String, `type`: String, extension: String, classifier: Option[String],
  //   configurations: Iterable[Configuration], url: Option[URL], extraAttributes: Map[String, String]) {
  //   def extra(attributes: (String, String)*) = copy(extraAttributes = extraAttributes ++ ModuleID.checkE(attributes))
  // }
  implicit object ArtifactFormat extends JsonFormat[Artifact] {
    def write[J](x: Artifact, builder: Builder[J]): Unit = {
      builder.beginObject()
      builder.addField("name", x.name)
      builder.addField("type", x.`type`)
      builder.addField("extension", x.extension)
      builder.addField("classifier", x.classifier)
      builder.addField("configurations", x.configurations.toList)
      builder.addField("url", x.url)
      builder.addField("extraAttributes", x.extraAttributes)
      builder.endObject()
    }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Artifact =
      jsOpt match {
        case Some(js) =>
          unbuilder.beginObject(js)
          val name = unbuilder.readField[String]("name")
          val `type` = unbuilder.readField[String]("type")
          val extension = unbuilder.readField[String]("extension")
          val classifier = unbuilder.readField[Option[String]]("classifier")
          val configurations = unbuilder.readField[List[Configuration]]("configurations")
          val url = unbuilder.readField[Option[URL]]("url")
          val extraAttributes = unbuilder.readField[Map[String, String]]("extraAttributes")
          unbuilder.endObject()
          Artifact(name, `type`, extension, classifier, configurations, url, extraAttributes)
        case None => deserializationError(s"Expected JsObject but got None")
      }
  }

  // final case class InclExclRule(organization: String = "*", name: String = "*", artifact: String = "*",
  // configurations: Seq[String] = Nil)
  implicit object InclExclRuleFormat extends JsonFormat[InclExclRule] {
    val emptyRule = InclExclRule()
    def write[J](x: InclExclRule, builder: Builder[J]): Unit = {
      builder.beginObject()
      builder.addField("organization", x.organization)
      builder.addField("name", x.name)
      builder.addField("artifact", x.artifact)
      builder.addField("configurations", x.configurations)
      builder.endObject()
    }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): InclExclRule =
      jsOpt match {
        case Some(js) =>
          unbuilder.beginObject(js)
          val organization = unbuilder.readField[String]("organization")
          val name = unbuilder.readField[String]("name")
          val artifact = unbuilder.readField[String]("artifact")
          val configurations = unbuilder.readField[Seq[String]]("configurations")
          unbuilder.endObject()
          InclExclRule(organization, name, artifact, configurations)
        case None => emptyRule
      }
  }

  /*
  final case class ModuleID(organization: String,
    name: String,
    revision: String,
    configurations: Option[String] = None,
    isChanging: Boolean = false,
    isTransitive: Boolean = true,
    isForce: Boolean = false,
    explicitArtifacts: Seq[Artifact] = Nil,
    inclusions: Seq[InclusionRule] = Nil,
    exclusions: Seq[ExclusionRule] = Nil,
    extraAttributes: Map[String, String] = Map.empty,
    crossVersion: CrossVersion = CrossVersion.Disabled,
    branchName: Option[String] = None)
  */
  implicit object ModuleIDFormat extends JsonFormat[ModuleID] {
    def write[J](x: ModuleID, builder: Builder[J]): Unit = {
      builder.beginObject()
      builder.addField("organization", x.organization)
      builder.addField("name", x.name)
      builder.addField("revision", x.revision)
      builder.addField("configurations", x.configurations)
      if (x.isChanging) {
        builder.addFieldName("isChanging")
        builder.writeBoolean(x.isChanging)
      }
      if (!x.isTransitive) {
        builder.addFieldName("isTransitive")
        builder.writeBoolean(x.isTransitive)
      }
      if (x.isForce) {
        builder.addFieldName("isForce")
        builder.writeBoolean(x.isForce)
      }
      builder.addField("explicitArtifacts", x.explicitArtifacts)
      builder.addField("inclusions", x.inclusions)
      builder.addField("exclusions", x.exclusions)
      builder.addField("extraAttributes", x.extraAttributes)
      if (x.crossVersion != CrossVersion.Disabled) {
        builder.addField("crossVersion", x.crossVersion)
      }
      builder.addField("branchName", x.branchName)
      builder.endObject()
    }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): ModuleID =
      jsOpt match {
        case Some(js) =>
          unbuilder.beginObject(js)
          val organization = unbuilder.readField[String]("organization")
          val name = unbuilder.readField[String]("name")
          val revision = unbuilder.readField[String]("revision")
          val configurations = unbuilder.readField[Option[String]]("configurations")
          val isChanging = unbuilder.lookupField("isChanging") match {
            case Some(x) => unbuilder.readBoolean(x)
            case _       => false
          }
          val isTransitive = unbuilder.lookupField("isTransitive") match {
            case Some(x) => unbuilder.readBoolean(x)
            case _       => true
          }
          val isForce = unbuilder.lookupField("isForce") match {
            case Some(x) => unbuilder.readBoolean(x)
            case _       => false
          }
          val explicitArtifacts = unbuilder.readField[Seq[Artifact]]("explicitArtifacts")
          val inclusions = unbuilder.readField[Seq[InclusionRule]]("inclusions")
          val exclusions = unbuilder.readField[Seq[ExclusionRule]]("exclusions")
          val extraAttributes = unbuilder.readField[Map[String, String]]("extraAttributes")
          val crossVersion = unbuilder.readField[CrossVersion]("crossVersion")
          val branchName = unbuilder.readField[Option[String]]("branchName")
          unbuilder.endObject()
          ModuleID(organization, name, revision, configurations,
            isChanging, isTransitive, isForce, explicitArtifacts,
            inclusions, exclusions, extraAttributes, crossVersion, branchName)
        case None => deserializationError(s"Expected JsObject but got None")
      }
  }
}

object LibraryManagementProtocol extends LibraryManagementProtocol
