package services

import com.gu.facia.api.models._
import com.gu.facia.api.utils.{ContentProperties, ResolvedMetaData, ItemKicker}
import com.gu.facia.client.models.TrailMetaData

object FaciaContentConvert {
  def frontentContentToFaciaContent(frontendContent: model.Content, maybeCollectionConfig: Option[CollectionConfig] = None): FaciaContent = {
    val trailMetaData = frontendContent.apiContent.metaData.getOrElse(TrailMetaData.empty)
    val cardStyle = com.gu.facia.api.utils.CardStyle(frontendContent.delegate, trailMetaData)
    val resolvedMetaData = ResolvedMetaData.fromContentAndTrailMetaData(frontendContent.apiContent.delegate, trailMetaData, cardStyle)
    val contentApiContent = frontendContent.apiContent.delegate

    frontendContent match {
      case snap: model.Snap =>
        LinkSnap(
          id = snap.id,
          snapType = snap.snapType.getOrElse("link"),
          snapUri = snap.snapUri,
          snapCss = snap.snapCss,
          headline = Option(snap.headline),
          href = snap.href,
          trailText = snap.trailText,
          group = snap.group.getOrElse("0"),
          image = None,
          isBreaking = snap.isBreaking,
          isBoosted = snap.isBoosted,
          showMainVideo = snap.showMainVideo,
          showKickerTag = snap.showKickerTag,
          byline = snap.byline,
          showByLine = snap.showByline,
          kicker = ItemKicker.fromContentAndTrail(Option(contentApiContent), trailMetaData, resolvedMetaData, maybeCollectionConfig),
          showBoostedHeadline = snap.showBoostedHeadline,
          showQuotedHeadline = snap.showQuotedHeadline
        )
      case other =>
        CuratedContent(
          content = frontendContent.apiContent.delegate,
          supportingContent = frontendContent.apiContent.supporting.map(FaciaContentConvert.frontentContentToFaciaContent(_, maybeCollectionConfig)),
          cardStyle = com.gu.facia.api.utils.CardStyle(frontendContent.delegate, trailMetaData),
          headline = frontendContent.headline,
          href = Option(frontendContent.id),
          trailText = frontendContent.trailText,
          group = frontendContent.group.getOrElse("0"),
          image = FaciaImage.getFaciaImage(Option(frontendContent.delegate), trailMetaData, resolvedMetaData),
          ContentProperties.fromResolvedMetaData(resolvedMetaData),
          frontendContent.byline,
          kicker = ItemKicker.fromContentAndTrail(Option(contentApiContent), trailMetaData, resolvedMetaData, maybeCollectionConfig),
          embedType = frontendContent.snapType,
          embedUri = frontendContent.snapUri,
          embedCss = frontendContent.snapCss)}


  }

  def frontentContentToFaciaContent(frontendContent: model.Content): FaciaContent = frontentContentToFaciaContent(frontendContent, None)
}