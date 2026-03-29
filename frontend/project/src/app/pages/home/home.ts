import { Component, inject } from '@angular/core';
import { Topbar } from '../../layout/topbar/topbar';
import { StatsRow } from "../../components/stats-row/stats-row";
import { NavigationCards } from "../../components/navigation-cards/navigation-cards";
import { RecentActivity } from "../../components/recent-activity/recent-activity";
import { RequestForm } from '../../service/request-form';
import { UserService } from '../../service/user-service';

@Component({
  selector: 'app-home',
  imports: [Topbar, StatsRow, NavigationCards, RecentActivity],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  private userService = inject(UserService);


}
