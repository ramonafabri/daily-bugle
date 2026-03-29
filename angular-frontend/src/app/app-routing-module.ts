import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ArticleList} from './components/article-list/article-list';
import {Home} from './components/home/home';
import {ArticleDetail} from './components/article-detail/article-detail';
import {ArticleCreate} from './components/article-create/article-create';
import {Register} from './components/register/register';


const routes: Routes = [
  { path: '', component: Home },
  { path: 'article-list', component: ArticleList },
  { path: 'article/:id', component: ArticleDetail },
  { path:'article-create', component: ArticleCreate },
  { path: 'register', component: Register }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
