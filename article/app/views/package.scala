package views

import common.Edition
import layout.ContentWidths
import layout.ContentWidths.{Inline, Showcase, MainMedia, LiveBlogMedia}
import model.Article
import play.api.mvc.RequestHeader
import views.support._
import views.support.cleaner.{AmpEmbedCleaner, VideoEmbedCleaner, CmpParamCleaner, AmpAdCleaner}

object MainMediaWidths {

  def apply(article: Article): layout.WidthsByBreakpoint = {
    if (article.hasShowcaseMainElement && article.isFeature) {
      MainMedia.featureShowcase
    } else {
      val hinting = if (article.hasShowcaseMainElement) { Showcase } else { Inline }
      val relation = if (article.isLiveBlog) { LiveBlogMedia } else { MainMedia }
      ContentWidths.getWidthsFromContentElement(hinting, relation)
    }
  }

}

object MainCleaner {
 def apply(article: Article, html: String, amp: Boolean)(implicit request: RequestHeader) = {
      implicit val edition = Edition(request)
      withJsoup(BulletCleaner(html))(
        if (amp) AmpEmbedCleaner(article) else VideoEmbedCleaner(article),
        PictureCleaner(article, amp),
        MainFigCaptionCleaner
      )
  }
}

object BodyCleaner {
  def apply(article: Article, html: String, amp: Boolean)(implicit request: RequestHeader) = {
    implicit val edition = Edition(request)
    val cleaners = List(
      InBodyElementCleaner,
      InBodyLinkCleaner("in body link", amp),
      BlockNumberCleaner,
      new TweetCleaner(article, amp),
      WitnessCleaner,
      TagLinker(article),
      TableEmbedComplimentaryToP,
      R2VideoCleaner(article),
      PictureCleaner(article, amp),
      LiveBlogDateFormatter(article.isLiveBlog),
      LiveBlogLinkedData(article.isLiveBlog),
      BloggerBylineImage(article),
      LiveBlogShareButtons(article),
      DropCaps(article.isComment || article.isFeature),
      FigCaptionCleaner,
      RichLinkCleaner,
      MembershipEventCleaner,
      BlockquoteCleaner,
      ChaptersLinksCleaner,
      PullquoteCleaner,
      CmpParamCleaner
    )
    val nonAmpCleaners = List(
      VideoEmbedCleaner(article)
    )
    val ampOnlyCleaners = List(
      AmpAdCleaner,
      AmpEmbedCleaner(article)
    )
    withJsoup(BulletCleaner(html))((if (amp) ampOnlyCleaners else nonAmpCleaners) ++ cleaners :_*)
  }
}
