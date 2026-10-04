import { Component } from '@angular/core';
import { HeaderComponent } from '../../utils/header/header.component';
import { FooterComponent } from '../../utils/footer/footer.component';
import { HeroComponent } from './components/hero/hero.component';
import { ServicesComponent } from './components/services/services.component';
import { TeamComponent } from './components/team/team.component';
import { HowItWorksComponent } from './components/how-it-works/how-it-works.component';
import { LocationComponent } from './components/location/location.component';

@Component({
  selector: 'app-landing-page',
  imports: [
    HeaderComponent,
    FooterComponent,
    HeroComponent,
    ServicesComponent,
    TeamComponent,
    HowItWorksComponent,
    LocationComponent
  ],
  templateUrl: './landing-page.component.html'
})
export class LandingPageComponent {}
