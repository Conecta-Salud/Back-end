OBSOLETO: reemplazado por el seed mínimo manual oficial y procesadores CSV actuales.

# Indicator Refactor Notes

The backend no longer reads `state_indicators` or `municipality_indicators`.
Dashboard, map, and comparison modules now use `territory_indicator_values`
through `TerritoryIndicatorQueryRepository`.

Frontend follow-up:

- Map responses keep the existing fields and now may include `sourceYear`,
  `unit`, `availabilityStatus`, `methodologyNote`, and `dataSourceName`.
- Indicator fields can be `null` when `data_availability` marks an indicator
  as unavailable for the requested territorial level and analysis year.
- Municipal poverty and healthcare access deficiency values must not be
  rendered as zero when unavailable; treat `null` as not available.
