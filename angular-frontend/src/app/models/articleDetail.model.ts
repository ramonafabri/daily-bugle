export interface ArticleDetailModel {

  id:number;
  author:string;
  title:string;
  synopsis:string;
  content:string;
  createdAt:string;
  comments:any[];
  averageRating:number;
  ratingCount:number;
  category:string;
  publishAt:string;
  keywords:string[];
  userRating?: number;
}
