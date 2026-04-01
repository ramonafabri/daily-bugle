import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ArticleList} from './components/article-list/article-list';
import {Home} from './components/home/home';
import {ArticleDetail} from './components/article-detail/article-detail';
import {ArticleCreate} from './components/article-create/article-create';
import {Register} from './components/register/register';
import {Login} from './components/login/login';
import {authGuard} from './guards/auth-guard';
import {ArticleSearch} from './components/article-search/article-search';


const routes: Routes = [
  { path: '', component: Home, pathMatch: 'full' },
  { path: 'article-list', component: ArticleList },
  { path: 'article/:id', component: ArticleDetail },
  { path:'article-create', component: ArticleCreate, canActivate: [authGuard]  },
  { path: 'register', component: Register },
  { path: 'login', component: Login },
  { path: 'article-search', component: ArticleSearch }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
