import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Navbar } from './components/navbar/navbar';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { ArticleList } from './components/article-list/article-list';
import { Home } from './components/home/home';
import { ArticleDetail } from './components/article-detail/article-detail';
import { ArticleCreate } from './components/article-create/article-create';
import { Register } from './components/register/register';

@NgModule({
  declarations: [App, Navbar, ArticleList, Home, ArticleDetail, ArticleCreate, Register],
  imports: [BrowserModule, AppRoutingModule, ReactiveFormsModule],
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient(withInterceptorsFromDi())],
  bootstrap: [App],
})
export class AppModule {}
