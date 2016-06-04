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
      builder.addField("organization")
      builder.writeString(x.organization)
      builder.addField("name")
      builder.writeString(x.name)
      builder.addField("revision")
      builder.writeString(x.revision)
      x.configurations map { cs =>
        builder.addField("configurations")
        builder.writeString(cs)
      }
      if (x.isChanging) {
        builder.addField("isChanging")
        builder.writeBoolean(x.isChanging)
      }
      if (!x.isTransitive) {
        builder.addField("isTransitive")
        builder.writeBoolean(x.isTransitive)
      }
      if (x.isForce) {
        builder.addField("isForce")
        builder.writeBoolean(x.isForce)
      }
      builder.endObject()
    }
    def read[J](js: J, unbuilder: Unbuilder[J]): ModuleID = {
      unbuilder.beginObject(js)
      val organization = unbuilder.lookupField("organization") match {
        case Some(x) => unbuilder.readString(x)
        case _       => deserializationError(s"Missing field: organization")
      }
      val name = unbuilder.lookupField("name") match {
        case Some(x) => unbuilder.readString(x)
        case _       => deserializationError(s"Missing field: name")
      }
      val revision = unbuilder.lookupField("revision") match {
        case Some(x) => unbuilder.readString(x)
        case _       => deserializationError(s"Missing field: revision")
      }
      val configurations = unbuilder.lookupField("configurations") match {
        case Some(x) => Some(unbuilder.readString(x))
        case _       => None
      }
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
    }
  }
}

object LibraryManagementProtocol extends LibraryManagementProtocol
