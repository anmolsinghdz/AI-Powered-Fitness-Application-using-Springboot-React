import React, { useEffect, useState } from "react";
import { useParams } from "react-router";
import { getActivityDetail } from "../services/api";

import {
  Card,
  CardContent,
  Container,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Typography,
} from "@mui/material";

import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import FitnessCenterIcon from "@mui/icons-material/FitnessCenter";
import HealthAndSafetyIcon from "@mui/icons-material/HealthAndSafety";

const ActivityDetail = () => {
  const { id } = useParams();
  const [activity, setActivity] = useState(null);

  useEffect(() => {
    const fetchActivityDetail = async () => {
      try {
        const response = await getActivityDetail(id);
        setActivity(response.data);
      } catch (error) {
        console.error(error);
      }
    };

    fetchActivityDetail();
  }, [id]);

  if (!activity) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>

      {/* Summary */}
      <Card sx={{ mb: 3 }}>
        <CardContent>

          <Typography variant="h4" fontWeight="bold">
            🚴 {activity.activityType}
          </Typography>

          <Typography color="text.secondary">
            {new Date(activity.createdAt).toLocaleString()}
          </Typography>

          <Divider sx={{ my: 2 }} />

          <Typography variant="h6" gutterBottom>
            AI Assessment
          </Typography>

          <Typography sx={{ whiteSpace: "pre-line" }}>
            {activity.recommendation}
          </Typography>

        </CardContent>
      </Card>

      {/* Improvements */}
      <Card sx={{ mb: 3 }}>
        <CardContent>

          <Typography variant="h6" gutterBottom>
            Areas for Improvement
          </Typography>

          <List>
            {activity.improvements.map((item, index) => (
              <ListItem key={index}>
                <ListItemIcon>
                  <TrendingUpIcon color="primary" />
                </ListItemIcon>

                <ListItemText primary={item} />
              </ListItem>
            ))}
          </List>

        </CardContent>
      </Card>

      {/* Suggestions */}
      <Card sx={{ mb: 3 }}>
        <CardContent>

          <Typography variant="h6" gutterBottom>
            Recommended Workouts
          </Typography>

          <List>
            {activity.suggestions.map((item, index) => (
              <ListItem key={index}>
                <ListItemIcon>
                  <FitnessCenterIcon color="success" />
                </ListItemIcon>

                <ListItemText primary={item} />
              </ListItem>
            ))}
          </List>

        </CardContent>
      </Card>

      {/* Safety */}
      <Card>
        <CardContent>

          <Typography variant="h6" gutterBottom>
            Safety Guidelines
          </Typography>

          <List>
            {activity.safety.map((item, index) => (
              <ListItem key={index}>
                <ListItemIcon>
                  <HealthAndSafetyIcon color="warning" />
                </ListItemIcon>

                <ListItemText primary={item} />
              </ListItem>
            ))}
          </List>

        </CardContent>
      </Card>

    </Container>
  );
};

export default ActivityDetail;