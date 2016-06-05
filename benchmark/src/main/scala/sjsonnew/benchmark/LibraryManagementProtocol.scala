package sjsonnew
package benchmark

import sbt.librarymanagement._

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

trait LibraryManagementProtocol extends BasicJsonProtocol {
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
          unbuilder.endObject()
          ModuleID(organization, name, revision, configurations,
            isChanging, isTransitive, isForce)
        case None => deserializationError(s"Expected JsObject but got None")
      }
  }
}

object LibraryManagementProtocol extends LibraryManagementProtocol
