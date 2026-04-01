import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Navbar } from './components/navbar/navbar';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ArticleList } from './components/article-list/article-list';
import { Home } from './components/home/home';
import { ArticleDetail } from './components/article-detail/article-detail';
import { ArticleCreate } from './components/article-create/article-create';
import { Register } from './components/register/register';
import { Login } from './components/login/login';
import { authInterceptor } from './interceptors/auth-interceptor';
import { ArticleSearch } from './components/article-search/article-search';
import {NgOptimizedImage} from '@angular/common';

@NgModule({
  declarations: [
    App,
    Navbar,
    ArticleList,
    Home,
    ArticleDetail,
    ArticleCreate,
    Register,
    Login,
    ArticleSearch,
  ],
  imports: [BrowserModule, AppRoutingModule, ReactiveFormsModule, FormsModule, NgOptimizedImage],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
  bootstrap: [App],
})
export class AppModule {}
