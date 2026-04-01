import {ArticleDetailModel} from './articleDetail.model';

export interface HomeResponseModel {


  latest : ArticleDetailModel[];
  topRated : ArticleDetailModel[];
  topRatedLast3Days?: ArticleDetailModel[];

}
